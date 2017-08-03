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

case class Store(var _id: Long, name:String, addr: String, phone: String)
object Store {
  import org.mongodb.scala._
  import org.mongodb.scala.bson.codecs.Macros._
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.bson.codecs.configuration.CodecRegistries.{ fromRegistries, fromProviders }

  implicit val reads = Json.reads[Store]
  implicit val writes = Json.writes[Store]

  val colName = "store"
  val codecRegistry = fromRegistries(fromProviders(classOf[Store]), DEFAULT_CODEC_REGISTRY)

  def collection(implicit db: MongoDatabase) =
    db.getCollection[Store](colName).withCodecRegistry(codecRegistry)

  def init(colNames: Seq[String], ownerID: String)(implicit db: MongoDatabase) {
    if (!colNames.contains(colName)) {
      val f = db.createCollection(colName).toFuture()
      f.onFailure(errorHandler)
      f.onSuccess({
        case x: Completed =>
          val defaultStore = Store(0, "創始店", "地址", "123456789")
          newStore(defaultStore, ownerID)
      })
    }
  }

  import scala.concurrent._
  //Always add store to owner...
  def newStore(store: Store, ownerID: String)(implicit db: MongoDatabase) = {
    val storeID = Identity.getNewID(Identity.Store)
    store._id = storeID.seq
    val f = collection.insertOne(store).toFuture()
    f.onFailure(errorHandler)
    val f1 = User.addStore(ownerID, store._id)
    f1.onFailure(errorHandler)
    Future.sequence(List(f, f1))
  }

  import org.mongodb.scala.model._
  def update(_id: Long, store: Store)(implicit db: MongoDatabase) = {
    assert(_id == store._id)

    val f = collection.replaceOne(Filters.equal("_id", _id), store).toFuture()
    f.onFailure(errorHandler)
    f
  }

  def getStoreList(skip: Int, limit: Int)(implicit db: MongoDatabase) = {
    val f = collection.find().skip(skip).limit(limit).toFuture()
    f.onFailure(errorHandler)
    f
  }

  def getAllStores(implicit db: MongoDatabase) = getStoreList(0, 1000)

  def getStoreList(storeIDs: Seq[Long])(implicit db: MongoDatabase) = {
    val f = collection.find(Filters.in("_id", storeIDs)).toFuture()
    f.onFailure(errorHandler)
    f
  }

  def getUserStoreList(usr: User)(implicit db: MongoDatabase) = {
    val groupID = Group.withName(usr.groupID)
    if (groupID == Group.Owner || groupID == Group.Admin) {
      getAllStores
    } else if (groupID == Group.Manager || groupID == Group.Clerk) {
      getStoreList(usr.storeList)
    } else
      throw new Exception(s"Unknow groupID ${usr.groupID}")
  }

  import org.mongodb.scala.model.Filters._
  def deleteStore(_id: Long)(implicit db: MongoDatabase) = {
    val f = collection.deleteOne(equal("_id", _id)).toFuture()
    f.onFailure(errorHandler("deleteStore"))
    f
  }

}