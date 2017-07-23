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

case class Identity(_id: String, seq: Int)

object Identity {
  import scala.concurrent._
  import scala.concurrent.duration._

  import org.mongodb.scala.bson.codecs.Macros._
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.bson.codecs.configuration.CodecRegistries.{ fromRegistries, fromProviders }
  import org.mongodb.scala._

  val colName = "identity"
  val codecRegistry = fromRegistries(fromProviders(classOf[Identity]), DEFAULT_CODEC_REGISTRY)
  def collection(implicit db: MongoDatabase) = db.getCollection[Identity](colName).withCodecRegistry(codecRegistry)

  val Database = "database"
  val User = "user"

  def init(colNames: Seq[String]) = {
    if (!colNames.contains(colName)) {
      val f = MongoDB.masterDB.createCollection(colName).toFuture()
      f.onFailure(errorHandler)
    }
    implicit val db = MongoDB.masterDB
    val f = collection.count().toFuture()
    f.onSuccess({
      case count: Long =>
        if (count == 0) {
          val id1 = Identity(Database, 1)
          val id2 = Identity(User, 1)
          newID(id1)
          newID(id2)
        }
    })
    f.onFailure(errorHandler)
    f
  }

  val Store = "store"
  val Customer = "customer"
  val Order = "order"

  def initCompanyDB(colNames: Seq[String])(implicit db: MongoDatabase) = {
    if (!colNames.contains(colName)) {
      val f = db.createCollection(colName).toFuture()
      f.onFailure(errorHandler)
    }

    val f = collection.count().toFuture()
    f.onSuccess({
      case count: Long =>
        if (count == 0) {
          val id1 = Identity(Store, 1)
          val id2 = Identity(Customer, 1)
          val id3 = Identity(Order, 1)
          newID(id1)
          newID(id2)
          newID(id3)
        }
    })
    f.onFailure(errorHandler)
    f
  }

  def newID(id: Identity)(implicit db: MongoDatabase) = {
    collection.insertOne(id).toFuture()
  }

  def getNewID(name: String)(implicit db: MongoDatabase) = {
    import org.mongodb.scala.model.Filters._
    import org.mongodb.scala.model._

    val f = collection.findOneAndUpdate(equal("_id", name), Updates.inc("seq", 1)).toFuture()
    val ids = waitReadyResult(f)
    ids
  }
}
