package models
import play.api._
import com.github.nscala_time.time.Imports._
import models.ModelHelper._
import models._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions
import org.mongodb.scala.bson._
import scala.concurrent._

case class EventLog(time: Long, usr: Option[String], store: Option[String],
                    level: Int, desc: String, postAction: Option[Int])


object EventLog {
  import org.mongodb.scala._
  import org.mongodb.scala.bson.codecs.Macros._
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.bson.codecs.configuration.CodecRegistries.{ fromRegistries, fromProviders }

  implicit val reads = Json.reads[EventLog]
  implicit val writes = Json.writes[EventLog]

  val colName = "eventLog"
  val codecRegistry = fromRegistries(fromProviders(classOf[EventLog]), DEFAULT_CODEC_REGISTRY)

  def collection(implicit db: MongoDatabase) =
    db.getCollection[EventLog](colName).withCodecRegistry(codecRegistry)

  import org.mongodb.scala.model._
  def init(colNames: Seq[String])(implicit db: MongoDatabase) {
    if (!colNames.contains(colName)) {
      val f = db.createCollection(colName).toFuture()
      f.onFailure(errorHandler)
      f.onSuccess({
        case x =>
          collection.createIndex(Indexes.ascending("time", "usr")).toFuture()
          collection.createIndex(Indexes.ascending("time", "store")).toFuture()
          collection.createIndex(Indexes.ascending("time", "level")).toFuture()
          log(None, None, 0, "Init", None)
      })
    }
  }

  def log(usr: Option[String], store: Option[String], level: Int, desc: String, postAction: Option[Int])(implicit db: MongoDatabase): Future[Completed] = {
    val time = DateTime.now().getMillis
    val evtLog = EventLog(time = time,
      usr = usr,
      store = store,
      level = level,
      desc = desc,
      postAction = postAction)
    log(evtLog)
  }

  def log(evtLog: EventLog)(implicit db: MongoDatabase) = {
    val f = collection.insertOne(evtLog).toFuture()
    f.onFailure(errorHandler)
    f
  }
}