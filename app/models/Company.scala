package models
import play.api._
import models.ModelHelper._
import models._
import org.mongodb.scala.bson.Document
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json
import org.mongodb.scala._
import scala.concurrent.ExecutionContext.Implicits.global

case class Company(_id: String, dbName: String, ownerID: String) {
  def database: MongoDatabase = MongoDB.mongoClient.getDatabase(dbName)
  def initDB = {
    val f = database.listCollectionNames().toFuture()
    val colFuture = f.map { colNames =>
      implicit val db = database
      val f = Identity.initCompanyDB(colNames)
      ModelHelper.waitReadyResult(f)

      Store.init(colNames, ownerID)
      EventLog.init(colNames)
    }
    //Program need to wait before init complete
    import scala.concurrent.Await
    import scala.concurrent.duration._
    import scala.language.postfixOps

    Await.result(colFuture, 30 seconds)
  }
}

object Company {
  import org.mongodb.scala.bson.codecs.Macros._
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.bson.codecs.configuration.CodecRegistries.{ fromRegistries, fromProviders }

  val colName = "company"
  val codecRegistry = fromRegistries(fromProviders(classOf[Company]), DEFAULT_CODEC_REGISTRY)
  val collection = MongoDB.masterDB.getCollection[Company](colName).withCodecRegistry(codecRegistry)

  implicit val read = Json.reads[Company]
  implicit val write = Json.writes[Company]

  def dropExistingClientDB() = {
    val dbNamesF = MongoDB.getDatabaseNameList
    val actionF =
      for (dbNames <- dbNamesF) yield {
        dbNames.foreach { dbName =>
          val pattern = """DB\d{6}""".r
          val result = pattern.findFirstMatchIn(dbName)
          if (result.isDefined) {
            Logger.info(s"remove exising client DB ${dbName}")
            MongoDB.mongoClient.getDatabase(dbName).drop().toFuture()
          }
        }
      }
    waitReadyResult(actionF)
  }
  
  def init(colNames: Seq[String]) {
    if (!colNames.contains(colName)) {
      Logger.info("Init company collection")
      dropExistingClientDB

      val f = MongoDB.masterDB.createCollection(colName).toFuture()
    }
  }

  def create(company: String, ownerID: String) = {
    def getDbName: String = {
      val seq = Identity.getNewID(Identity.Database)(MongoDB.masterDB).seq
      "DB%06d".format(seq)
    }
    Logger.info(s"Create $company company for $ownerID")
    createCompany(Company(company, getDbName, ownerID))
  }

  private def createCompany(company: Company) = {
    val f = collection.insertOne(company).toFuture()
    f.onSuccess({
      case _ =>
        company.initDB
    })
    f
  }

  def listAllCompanies = {
    val f = collection.find().toFuture()
    for (companies <- f) yield companies
  }

  import org.mongodb.scala.model.Filters._
  def findCompany(_id: String) = {
    val f = collection.find(equal("_id", _id)).toFuture()
    for (companies <- f) yield {
      if (companies.isEmpty)
        None
      else
        Some(companies(0))
    }
  }
}