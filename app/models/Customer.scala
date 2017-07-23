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

case class Vacine(name:String, time:Long)
case class PetChip(time:Long)
case class PetRecord(weight:Double, time:Long)
case class Pet(name:String, breed:String, color:String, records:Seq[PetRecord], bdate:Option[Long], 
    habit:Option[String], hospital:Option[String], chip:Option[PetChip], vacineList:Seq[Vacine])
case class Customer(var _id: Long, name:String, bdate:Option[Long], addr: Option[String], phone: Option[String],
    facebook:Option[String], line:Option[String], email:Option[String], note:Option[String], petList:Seq[Pet], 
    var firstDate:Long, var lastTime:Long)
object Customer {
  import org.mongodb.scala._
  import org.mongodb.scala.bson.codecs.Macros._
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.bson.codecs.configuration.CodecRegistries.{ fromRegistries, fromProviders }

  val colName = "customer"
  val codecRegistry = fromRegistries(
      fromProviders(classOf[Customer], classOf[Pet], 
          classOf[PetRecord], classOf[PetChip], classOf[Vacine]), DEFAULT_CODEC_REGISTRY)

  def collection(implicit db: MongoDatabase) =
    db.getCollection[Customer](colName).withCodecRegistry(codecRegistry)

  import org.mongodb.scala.model._
  def init(colNames: Seq[String])(implicit db: MongoDatabase) {
    if (!colNames.contains(colName)) {
      val f = db.createCollection(colName).toFuture()
      f.onFailure(errorHandler)
      f.onSuccess({
        case x: Completed =>
          collection.createIndex(Indexes.ascending("name"))
          collection.createIndex(Indexes.ascending("phone"))
          collection.createIndex(Indexes.ascending("line"))
          collection.createIndex(Indexes.descending("lastTime"))
      })
    }
  }

  import scala.concurrent._
  
  def newCustomer(customer: Customer)(implicit db: MongoDatabase) = {
    val id = Identity.getNewID(Identity.Customer)
    customer._id = id.seq
    customer.firstDate = DateTime.now().getMillis
    customer.lastTime = customer.firstDate
    val f = collection.insertOne(customer).toFuture()
    f.onFailure(errorHandler)
    f
  }

  import org.mongodb.scala.model._
  def update(_id: String, customer: Customer)(implicit db: MongoDatabase) = {
    assert(_id == customer._id)
    customer.lastTime = DateTime.now().getMillis
    val f = collection.replaceOne(Filters.equal("_id", _id), customer, UpdateOptions().upsert(true)).toFuture()
    f.onFailure(errorHandler)
    f
  }

  def getCustomerList(skip: Int, limit: Int)(implicit db: MongoDatabase) = {
    val f = collection.find().sort(Sorts.ascending("lastTime")).skip(skip).limit(limit).toFuture()
    f.onFailure(errorHandler)
    f
  }

  import org.mongodb.scala.model.Filters._
  def deleteCustomer(_id: String)(implicit db: MongoDatabase) = {
    val f = collection.deleteOne(equal("_id", _id)).toFuture()
    f.onFailure(errorHandler("deleteCustomer"))
    f
  }
}