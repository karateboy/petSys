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

case class Store(_id: String, name: String, addr: String, phone: String, managers: Seq[String], clerks: Seq[String])
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

  def init(colNames: Seq[String], owners: Seq[String])(implicit db: MongoDatabase) {
    if (!colNames.contains(colName)) {
      val f = db.createCollection(colName).toFuture()
      f.onFailure(errorHandler)
      f.onSuccess({
        case x: Completed =>
          val id = Identity.getNewID(Identity.Store)
          val defaultStore = Store(s"${id.seq}", s"${id.seq}號店", "地址", "123456789", owners, Seq.empty[String])
          newStore(defaultStore)
      })
    }
  }

  def newStore(store: Store)(implicit db: MongoDatabase) = {
    val f = collection.insertOne(store).toFuture()
    f.onFailure(errorHandler)
    f
  }

  def addManager(storeID:String, usrID:String)(implicit db: MongoDatabase) = addPerson(storeID, usrID, "managers")
  def addClerk(storeID:String, usrID:String)(implicit db: MongoDatabase) = addPerson(storeID, usrID, "clerks")
  private def addPerson(storeID:String, usrID:String, personType:String)(implicit db: MongoDatabase)={
    import org.mongodb.scala.model._
    val f = collection.updateOne(Filters.equal("_id", storeID), Updates.addToSet(personType, usrID)).toFuture()
    f.onFailure(errorHandler)
    f
  }

  def removeManager(storeID:String, usrID:String)(implicit db: MongoDatabase) = removePerson(storeID, usrID, "managers")
  def removeClerk(storeID:String, usrID:String)(implicit db: MongoDatabase) = removePerson(storeID, usrID, "clerks")
  private def removePerson(storeID:String, usrID:String, personType:String)(implicit db: MongoDatabase)={
    import org.mongodb.scala.model._
    val f = collection.updateOne(Filters.equal("_id", storeID), Updates.pull(personType, usrID)).toFuture()
    f.onFailure(errorHandler)
    f
  }

  import org.mongodb.scala.model._
  def upsert(_id: String, newStore: Store)(implicit db: MongoDatabase) = {
    val f = collection.replaceOne(Filters.equal("_id", _id), newStore, UpdateOptions().upsert(true)).toFuture()
    f.onFailure(errorHandler)
    f
  }

  def getStoreList(skip: Int, limit: Int)(implicit db: MongoDatabase) = {
    val f = collection.find().skip(skip).limit(limit).toFuture()
    f.onFailure(errorHandler)
    f
  }

  def getAllStores(implicit db: MongoDatabase) = getStoreList(0, 1000)

  def getUserStoreList(usr: User)(implicit db: MongoDatabase) = {
    if (usr.groupID == Group.Owner.toString() || usr.groupID == Group.Admin.toString()) {
      getAllStores
    } else if (usr.groupID == Group.Manager.toString()) {
      getAllStores.map { _.filter { _.managers.contains(usr._id) } }
    } else if (usr.groupID == Group.Clerk.toString()) {
      getAllStores.map { _.filter { _.clerks.contains(usr._id) } }
    } else
      throw new Exception(s"Unknow groupID ${usr.groupID}")
  }
}