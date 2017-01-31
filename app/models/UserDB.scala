package models
import org.mongodb.scala._
import scala.concurrent.ExecutionContext.Implicits.global

class UserDB(dbName: String) {
  val database: MongoDatabase = MongoDB.mongoClient.getDatabase(dbName)
  def init() {
    val f = database.listCollectionNames().toFuture()
    val colFuture = f.map { colNames =>
      
    }
    //Program need to wait before init complete
    import scala.concurrent.Await
    import scala.concurrent.duration._
    import scala.language.postfixOps

    Await.result(colFuture, 30 seconds)
  }
}

object UserDB {

}