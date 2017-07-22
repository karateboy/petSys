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
import org.mongodb.scala.model._
import org.mongodb.scala.MongoDatabase

case class User(_id: String, company: String, name: String, password: String,
                phone: String, email: String, groupID: String, storeList: Seq[String])

object User {
  import scala.concurrent._
  import scala.concurrent.duration._

  import org.mongodb.scala.bson.codecs.Macros._
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.bson.codecs.configuration.CodecRegistries.{ fromRegistries, fromProviders }

  def buildUserID(company: String, name: String) = s"$company#$name"
  implicit val reads = Json.reads[User]
  implicit val writes = Json.writes[User]

  val ADMIN_COMPANY = "admin"
  def buildCompanyUser(company: String, name: String, password: String,
                       phone: String, email: String, groupID: Group.Value, storeList: Seq[String]) =
    User(_id = buildUserID(company, name),
      company = company,
      name = name,
      password = password,
      phone = phone,
      email = email,
      groupID = groupID.toString,
      storeList = storeList)

  def buildAdminUser(name: String, password: String, phone: String, email: String, groupID: Group.Value) =
    User(_id = buildUserID(ADMIN_COMPANY, name),
      company = ADMIN_COMPANY,
      name = name,
      password = password,
      phone = phone,
      email = email,
      groupID = groupID.toString,
      storeList = Seq.empty[String])

  val colName = "user"

  val codecRegistry = fromRegistries(fromProviders(classOf[User]), DEFAULT_CODEC_REGISTRY)

  val collection = MongoDB.masterDB.getCollection[User](colName).withCodecRegistry(codecRegistry)

  val defaultAdmin = buildAdminUser("karateboy", "abc123", "0920660136",
    "karateboy.tw@gmail.com", Group.Admin)

  val defaultOwner = buildAdminUser("owner", "abc123", "0920660136",
    "karateboy.tw@gmail.com", Group.Owner)

  val defaultManager = buildAdminUser("manager", "abc123", "0920660136",
    "karateboy.tw@gmail.com", Group.Manager)

  val defaultClerk = buildAdminUser("clerk", "abc123", "0920660136",
    "karateboy.tw@gmail.com", Group.Clerk)

  def init(colNames: Seq[String]) {
    if (!colNames.contains(colName)) {
      val f = MongoDB.masterDB.createCollection(colName).toFuture()
      f.onFailure(errorHandler)
    }

    val f = collection.count().toFuture()
    f.onSuccess({
      case count: Long =>
        if (count == 0) {
          Logger.info("create default admin/owner/manager/clerk")
          val completeF = newCompanyOwner(defaultOwner)
          completeF.onSuccess({
            case x =>
              newUser(defaultAdmin)
              newUser(defaultManager)
              newUser(defaultClerk)
          })
        }
    })
    f.onFailure(errorHandler)
  }

  def newUser(user: User) = {
    val f = collection.insertOne(user).toFuture()
    f.onFailure(errorHandler("newUser"))
    f
  }

  def newCompanyOwner(user: User) = {
    val groupID = Group.withName(user.groupID)
    assert(groupID == Group.Owner)

    val companyOpt = waitReadyResult(Company.findCompany(user.company))
    if (companyOpt.isDefined) {
      throw new Exception("Company existed!")
    } else {
      val f1 = Company.create(user.company, user._id)
      val f2 = collection.insertOne(user).toFuture()
      Future.sequence(List(f1, f2))
    }
  }

  import org.mongodb.scala.model.Filters._
  def deleteUser(email: String) = {
    val f = collection.deleteOne(equal("_id", email)).toFuture()
    f.onFailure(errorHandler("deleteUser"))
    f
  }

  def updateUser(_id:String, user: User) = {
    val f = collection.replaceOne(equal("_id", _id), user).toFuture()
    f.onFailure(errorHandler("updateUser"))
    f
  }

  def getUserByID(_id: String) = {
    val f = collection.find(equal("_id", _id)).toFuture()
    f.onFailure(errorHandler("getUserByID"))

    for (user <- f)
      yield user
  }

  def getUserByCompanyAndName(company: String, name: String) = getUserByID(buildUserID(company, name))

  def getCompanyUsers(company: String)(skip: Int, limit: Int) = {
    import org.mongodb.scala.model._
    val f = collection.find(Filters.equal("company", company)).skip(skip).limit(limit).toFuture()
    f.onFailure(errorHandler("getCompanyUsers"))
    f
  }

  def getAdminUsers() = {
    val f = collection.find(equal("groupId", Group.Admin.toString)).toFuture()
    f.onFailure { errorHandler }
    for (users <- f)
      yield users
  }

  def addStore(userID: String, storeID: String) = {
    import org.mongodb.scala.model._
    val f = collection.updateOne(Filters.equal("_id", userID), Updates.addToSet("storeList", storeID)).toFuture()
    f.onFailure(errorHandler)
    f
  }

  def removeStore(userID: String, storeID: String) = {
    import org.mongodb.scala.model._
    val f = collection.updateOne(Filters.equal("_id", userID), Updates.pull("storeList", storeID)).toFuture()
    f.onFailure(errorHandler)
    f
  }
}
