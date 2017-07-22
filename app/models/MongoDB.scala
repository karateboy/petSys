package models
import play.api._
import scala.concurrent.ExecutionContext.Implicits.global

object MongoDB {
  import org.mongodb.scala._

  val url = Play.current.configuration.getString("my.mongodb.url")
  val dbName = Play.current.configuration.getString("my.mongodb.db")

  val mongoClient: MongoClient = MongoClient(url.get)
  val masterDB: MongoDatabase = mongoClient.getDatabase(dbName.get)
  def init() {
    val f = masterDB.listCollectionNames().toFuture()
    val colFuture = f.map { colNames =>
      val idF = Identity.init(colNames)
      idF.onComplete { x =>
        Company.init(colNames)
        User.init(colNames)
      }
    }
    //Program need to wait before init complete
    import scala.concurrent.Await
    import scala.concurrent.duration._
    import scala.language.postfixOps

    Await.result(colFuture, 30 seconds)
  }

  def isDatabaseNameExist(dbName: String) = {
    val f = mongoClient.listDatabaseNames().toFuture()
    for (dbNames <- f) yield {
      dbNames.contains(dbName)
    }
  }

  def getDatabaseNameList =
    mongoClient.listDatabaseNames().toFuture()

  def cleanup = {
    mongoClient.close()
  }
}