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

case class Order(var _id: Long, customerID: Int, pet: Pet, storeID: Long, services:Seq[String],                 
                 note: String, workers: Seq[String], var time: Long, active: Boolean)
object Order {
  import org.mongodb.scala._
  import org.mongodb.scala.bson.codecs.Macros._
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.bson.codecs.configuration.CodecRegistries.{ fromRegistries, fromProviders }

  implicit val writePet = Json.writes[Pet]
  implicit val readPet = Json.reads[Pet]
  implicit val reads = Json.reads[Order]
  implicit val writes = Json.writes[Order]

  val colName = "order"
  val codecRegistry = fromRegistries(fromProviders(classOf[Order], classOf[Pet]), DEFAULT_CODEC_REGISTRY)

  def collection(implicit db: MongoDatabase) =
    db.getCollection[Order](colName).withCodecRegistry(codecRegistry)

  import org.mongodb.scala.model._
  def init(colNames: Seq[String])(implicit db: MongoDatabase) {
    if (!colNames.contains(colName)) {
      val f = db.createCollection(colName).toFuture()
      f.onFailure(errorHandler)
      f.onSuccess({
        case x: Completed =>
          collection.createIndex(Indexes.descending("active", "store"))
          collection.createIndex(Indexes.descending("time", "store"))
      })
    }
  }

  import scala.concurrent._
  //Always add store to owner...
  def newOrder(order: Order)(implicit db: MongoDatabase) = {
    val id = Identity.getNewID(Identity.Order)
    order._id = id.seq
    order.time = DateTime.now().getMillis
    val f = collection.insertOne(order).toFuture()
    f.onFailure(errorHandler)
    f
  }

  import org.mongodb.scala.model._
  def update(_id: Long, order: Order)(implicit db: MongoDatabase) = {
    assert(_id == order._id)

    val f = collection.replaceOne(Filters.equal("_id", _id), order, UpdateOptions().upsert(true)).toFuture()
    f.onFailure(errorHandler)
    f
  }

  import org.mongodb.scala.model.Filters._
  import org.mongodb.scala.model.Sorts._
  import org.mongodb.scala.model._
  def getOrderList(skip: Int, limit: Int)(implicit db: MongoDatabase) = {
    val f = collection.find().sort(Sorts.descending("lastTime")).skip(skip).limit(limit).toFuture()
    f.onFailure(errorHandler)
    f
  }

  import org.mongodb.scala.model.Filters._
  def deleteOrder(_id: Long)(implicit db: MongoDatabase) = {
    val f = collection.deleteOne(equal("_id", _id)).toFuture()
    f.onFailure(errorHandler("delete "))
    f
  }

}