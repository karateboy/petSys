package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.json._
import com.github.nscala_time.time.Imports._
import models._
import ModelHelper._

object Application extends Controller {

  import models.User._

  def newUser = Security.Authenticated.async(BodyParsers.parse.json) {
    implicit request =>
      implicit val reads = Json.reads[User]
      checkPermission(Group.allowedNewUser)({
        val newUserParam = request.body.validate[User]

        newUserParam.fold(
          error => {
            Logger.error("bad param...")
            Logger.error(JsError.toJson(error).toString())
            Future { BadRequest(Json.obj("ok" -> false, "msg" -> JsError.toJson(error).toString())) }
          },
          param => {
            Logger.debug("new User")
            val userInfo = Security.getUserinfo(request).get
            assert(userInfo.company == param.company)
            val groupID = Group.withName(param.groupID)
            val newUser = buildCompanyUser(company = param.company,
              name = param.name,
              password = param.password,
              phone = param.phone,
              email = param.email,
              groupID = groupID,
              storeList = param.storeList)

            val f = User.newUser(newUser)
            f.onFailure(errorHandler)

            f.recover({
              case ex: Throwable =>
                Future {
                  Logger.error("newUser failed", ex)
                  Ok(Json.obj("ok" -> false, "msg" -> "使用者登入ID重複!"))

                }
            })

            for (result <- f) yield {
              Logger.info("add new user")
              Ok(Json.obj("ok" -> true))
            }
          })
      })
  }

  def deleteUser(encodedID: String) = Security.Authenticated.async {
    implicit request =>
      checkPermission(Group.allowedDelUser)({
        val userInfo = Security.getUserInfo().get
        val _id = java.net.URLDecoder.decode(encodedID, "UTF-8")
        assert(_id.startsWith(s"${userInfo.company}#"))
        val f = User.deleteUser(_id)
        val requestF =
          for (result <- f) yield {
            Ok(Json.obj("ok" -> (result.getDeletedCount == 1)))
          }

        requestF.recover({
          case _: Throwable =>
            Logger.info("recover from deleteUser error...")
            Ok(Json.obj("ok" -> false))
        })
      })
  }

  def updateUser(encodedID: String) = Security.Authenticated.async(BodyParsers.parse.json) {
    implicit request =>
      val _id = java.net.URLDecoder.decode(encodedID, "UTF-8")
      val userParam = request.body.validate[User]

      userParam.fold(
        error => {
          Future {
            Logger.error(JsError.toJson(error).toString())
            BadRequest(Json.obj("ok" -> false, "msg" -> JsError.toJson(error).toString()))
          }
        },
        param => {
          val userInfo = Security.getUserInfo().get
          val groupID = Group.withName(userInfo.groupID)
          if (userInfo.id == _id || Group.allowedNewUser.contains(groupID)) {
            val f = User.updateUser(_id, param)
            for (ret <- f) yield {
              Ok(Json.obj("ok" -> (ret.getMatchedCount == 1)))
            }
          } else {
            Future {
              Forbidden("無權限")
            }
          }
        })
  }

  def getCompanyUsers(company: String, skip: Int, limit: Int) = Security.Authenticated.async {
    implicit request =>
      val userInfo = Security.getUserInfo().get
      val userF = User.getCompanyUsers(company)(0, 1000)
      for (users <- userF) yield {
        val groupID = Group.withName(userInfo.groupID)
        groupID match {
          case Group.Admin =>
            Ok(Json.toJson(users))
          case Group.Owner =>
            Ok(Json.toJson(users))
          case Group.Manager =>
            val filterdUser = users.filter { usr =>
              val usrGroupID = Group.withName(usr.groupID)
              usrGroupID == Group.Clerk ||
                (usrGroupID == Group.Manager && userInfo.id == usr._id)
            }
            Ok(Json.toJson(filterdUser))
          case Group.Clerk =>
            val filterdUser = users.filter { usr =>
              val usrGroupID = Group.withName(usr.groupID)
              usrGroupID == Group.Clerk && userInfo.id == usr._id
            }
            Ok(Json.toJson(filterdUser))
        }
      }
  }

  def checkPermission[A, B <: UserInfo](allowedGroup: Seq[Group.Value])(permited: Future[Result])(implicit request: play.api.mvc.Security.AuthenticatedRequest[A, B]) = {
    val userInfoOpt = Security.getUserinfo(request)
    if (userInfoOpt.isEmpty)
      Future {
        Forbidden("無此使用者!!")
      }
    else {
      val userInfo = userInfoOpt.get
      val userF = User.getUserByID(userInfo.id)
      val users = ModelHelper.waitReadyResult(userF)

      if (users.length == 0)
        Future {
          Forbidden("無此使用者!")
        }
      else {
        val groupID = Group.withName(users(0).groupID)
        if (!allowedGroup.contains(groupID))
          Future {
            Forbidden("無權限!")
          }
        else {
          permited
        }
      }

    }
  }

  import scala.concurrent.ExecutionContext.Implicits.global
  def getGroupInfoList = Action {
    val infoList = Group.getInfoList
    implicit val write = Json.writes[GroupInfo]
    Ok(Json.toJson(infoList))
  }

  def getGroupInfoListBelow = Security.Authenticated {
    implicit request =>
      val userInfo = Security.getUserInfo().get
      val infoList = Group.getGroupInfoListBelow(Group.withName(userInfo.groupID))
      implicit val write = Json.writes[GroupInfo]
      Ok(Json.toJson(infoList))
  }

  def getCompanyDB[A, B <: UserInfo](implicit request: play.api.mvc.Security.AuthenticatedRequest[A, B]) = {
    val userInfoOpt = Security.getUserinfo(request)
    if (userInfoOpt.isEmpty)
      throw new Exception("無此使用者!!")
    else {
      val userInfo = userInfoOpt.get
      val userF = User.getUserByID(userInfo.id)

      for (users <- userF) yield {
        if (users.length == 0)
          throw new Exception("無此使用者!!")
        else if (users(0).company != userInfo.company)
          throw new Exception("使用者公司和資料庫內容不同!")
        else
          (MongoDB.mongoClient.getDatabase(userInfo.dbName), users(0))
      }
    }
  }

  import org.mongodb.scala.MongoDatabase
  def verifyUserInfo[A, B <: UserInfo](operation: (MongoDatabase, User) => Future[Result])(implicit request: play.api.mvc.Security.AuthenticatedRequest[A, B]) = {
    try {
      val dbUsrF = getCompanyDB
      val (db, usr) = ModelHelper.waitReadyResult(dbUsrF)
      operation(db, usr)
    } catch {
      case ex: Exception =>
        Logger.error("failed verifyUserInfo", ex)
        Future {
          Forbidden(ex.getMessage)
        }
    }
  }

  //Store
  def getUserStoreList = Security.Authenticated.async {
    implicit request =>
      verifyUserInfo((db: MongoDatabase, usr: User) => {
        implicit val companyDB = db
        val storeListF = Store.getUserStoreList(usr)

        for (storeList <- storeListF) yield Ok(Json.toJson(storeList))
      })
  }

  def newStore = Security.Authenticated.async(BodyParsers.parse.json) {
    implicit request =>
      implicit val reads = Json.reads[Store]
      checkPermission(Group.allowNewStore)({
        val newStoreParam = request.body.validate[Store]

        newStoreParam.fold(
          error => {
            Logger.error("bad param...")
            Logger.error(JsError.toJson(error).toString())
            Future { BadRequest(Json.obj("ok" -> false, "msg" -> JsError.toJson(error).toString())) }
          },
          store => {
            Logger.debug("new Store")
            val userInfo = Security.getUserinfo(request).get
            implicit val db = MongoDB.mongoClient.getDatabase(userInfo.dbName)
            val f = Store.newStore(store, userInfo.id)
            f.onFailure(errorHandler)

            f.recover({
              case ex: Throwable =>
                Future {
                  Logger.error("newUser failed", ex)
                  Ok(Json.obj("ok" -> false, "msg" -> "Store重複!"))
                }
            })

            for (result <- f) yield {
              Logger.info("add new store")
              Ok(Json.obj("ok" -> true))
            }
          })
      })
  }

  def deleteStore(encodedID: String) = Security.Authenticated.async {
    implicit request =>
      checkPermission(Group.allowedDelUser)({
        val userInfo = Security.getUserInfo().get
        implicit val db = userInfo.db
        val _id = java.net.URLDecoder.decode(encodedID, "UTF-8")
        val f = Store.deleteStore(_id)
        val requestF =
          for (result <- f) yield {
            Ok(Json.obj("ok" -> (result.getDeletedCount == 1)))
          }

        requestF.recover({
          case _: Throwable =>
            Logger.info("recover from deleteUser error...")
            Ok(Json.obj("ok" -> false))
        })
      })
  }

  def updateStore(encodedID: String) = Security.Authenticated.async(BodyParsers.parse.json) {
    implicit request =>
      val _id = java.net.URLDecoder.decode(encodedID, "UTF-8")
      val userParam = request.body.validate[Store]

      userParam.fold(
        error => {
          Future {
            Logger.error(JsError.toJson(error).toString())
            BadRequest(Json.obj("ok" -> false, "msg" -> JsError.toJson(error).toString()))
          }
        },
        param => {
          Logger.debug(s"update store _id=${_id}")
          val userInfo = Security.getUserInfo().get
          val groupID = Group.withName(userInfo.groupID)
          implicit val db = userInfo.db
          val f = Store.update(_id, param)
          for (ret <- f) yield {
            Ok(Json.obj("ok" -> (ret.getMatchedCount == 1)))
          }
        })
  }

  def newCustomer = Security.Authenticated.async(BodyParsers.parse.json) {
    implicit request =>
      import Customer._

      val customerParam = request.body.validate[Customer]
      customerParam.fold(
        error => {
          Future {
            Logger.error(JsError.toJson(error).toString())
            BadRequest(Json.obj("ok" -> false, "msg" -> JsError.toJson(error).toString()))
          }
        },
        customer => {
          Logger.debug(s"new customer")
          val userInfo = Security.getUserInfo().get
          val groupID = Group.withName(userInfo.groupID)
          implicit val db = userInfo.db
          val f = Customer.newCustomer(customer)
          for (ret <- f) yield {
            Ok(Json.obj("ok" -> true))
          }
        })
  }

  def updateCustomer(_id: String) = Security.Authenticated.async(BodyParsers.parse.json) {
    implicit request =>
      import Customer._

      val customerParam = request.body.validate[Customer]
      customerParam.fold(
        error => {
          Future {
            Logger.error(JsError.toJson(error).toString())
            BadRequest(Json.obj("ok" -> false, "msg" -> JsError.toJson(error).toString()))
          }
        },
        customer => {
          Logger.debug(s"update customer")
          val userInfo = Security.getUserInfo().get
          val groupID = Group.withName(userInfo.groupID)
          implicit val db = userInfo.db
          val f = Customer.update(_id, customer)
          for (ret <- f) yield {
            Ok(Json.obj("ok" -> (ret.getMatchedCount == 1)))
          }
        })
  }

  import Customer._
  def queryCustomer(skip: Int, limit: Int) = Security.Authenticated.async(BodyParsers.parse.json) {
    implicit request =>
      implicit val paramRead = Json.reads[QueryCustomerParam]
      val result = request.body.validate[QueryCustomerParam]
      result.fold(
        err =>
          Future {
            Logger.error(JsError.toJson(err).toString())
            BadRequest(JsError.toJson(err).toString())
          },
        param => {
          val userInfo = Security.getUserInfo().get
          val groupID = Group.withName(userInfo.groupID)
          implicit val db = userInfo.db

          val f = Customer.query(param)(skip, limit)
          for (customers <- f)
            yield Ok(Json.toJson(customers))
        })
  }
  def queryCustomerCount() = Security.Authenticated.async(BodyParsers.parse.json) {
    implicit request =>
      implicit val paramRead = Json.reads[QueryCustomerParam]
      val result = request.body.validate[QueryCustomerParam]
      result.fold(
        err =>
          Future {
            Logger.error(JsError.toJson(err).toString())
            BadRequest(JsError.toJson(err).toString())
          },
        param => {
          val userInfo = Security.getUserInfo().get
          val groupID = Group.withName(userInfo.groupID)
          implicit val db = userInfo.db

          val f = Customer.count(param)
          for (count <- f)
            yield Ok(Json.toJson(count))
        })
  }

}
