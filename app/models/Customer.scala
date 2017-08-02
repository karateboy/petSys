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

case class Pet(name: String, breed: String, color: Option[String], bdate: Option[Long],
               habit: Option[String], hospital: Option[String], chip: Option[Long], vacineList: Option[String])
case class Customer(var _id: Long, name: String, bdate: Option[Long], addr: Option[String], phone: Option[String],
                    email: Option[String], facebook: Option[String], line: Option[String], note: Option[String], petList: Seq[Pet], orderList: Seq[Long],
                    var firstTime: Long, var lastTime: Long)

object Customer {
  import org.mongodb.scala._
  import org.mongodb.scala.bson.codecs.Macros._
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.bson.codecs.configuration.CodecRegistries.{ fromRegistries, fromProviders }

  implicit val readPet = Json.reads[Pet]
  implicit val readCustomer = Json.reads[Customer]
  implicit val writePet = Json.writes[Pet]
  implicit val writeCustomer = Json.writes[Customer]

  val colName = "customer"
  val codecRegistry = fromRegistries(
    fromProviders(classOf[Customer], classOf[Pet]), DEFAULT_CODEC_REGISTRY)

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
    customer.firstTime = DateTime.now().getMillis
    customer.lastTime = customer.firstTime
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

  case class QueryCustomerParam(name: Option[String], phone: Option[String], start: Option[Long], end: Option[Long])
  def query(param:QueryCustomerParam)(skip: Int, limit: Int)(implicit db: MongoDatabase) = {
    import org.mongodb.scala.model.Filters._
    import org.mongodb.scala.model._

    val phoneFilter = param.phone map { phone => regex("phone", "(?i)" + phone) }
    val nameFilter = param.name map { name => regex("name", "(?i)" + name) }
    val startFilter = param.start map { start => gte("lastTime", start) }
    val endFilter = param.end map { end => lt("lastTime", end) }

    val filterList = List(nameFilter, startFilter, endFilter).flatMap { f => f }
    val filter = if (!filterList.isEmpty)
      and(filterList: _*)
    else
      Filters.exists("_id")

    val f = collection.find(filter).sort(Sorts.ascending("_id")).skip(skip).limit(limit).toFuture()
    f.onFailure {
      errorHandler
    }
    for (records <- f)
      yield records 
  }
  
  def count(param:QueryCustomerParam)(implicit db: MongoDatabase) = {
    import org.mongodb.scala.model.Filters._
    import org.mongodb.scala.model._

    val phoneFilter = param.phone map { phone => regex("phone", "(?i)" + phone) }
    val nameFilter = param.name map { name => regex("name", "(?i)" + name) }
    val startFilter = param.start map { start => gte("lastTime", start) }
    val endFilter = param.end map { end => lt("lastTime", end) }

    val filterList = List(nameFilter, startFilter, endFilter).flatMap { f => f }
    val filter = if (!filterList.isEmpty)
      and(filterList: _*)
    else
      Filters.exists("_id")

    val f = collection.count(filter).toFuture()
    f.onFailure {
      errorHandler
    }
    for (records <- f)
      yield records 
  }

}