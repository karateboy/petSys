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

  case class NewUserParam(name: String, password: String, phone: String, email: String,
                          groupID: String, storeList: Seq[String])

  def newUser = Security.Authenticated.async(BodyParsers.parse.json) {
    implicit request =>
      implicit val reads = Json.reads[NewUserParam]
      checkPermission(Group.allowedNewUser)({
        val newUserParam = request.body.validate[NewUserParam]

        newUserParam.fold(
          error => {
            Logger.error(JsError.toJson(error).toString())
            Future { BadRequest(Json.obj("ok" -> false, "msg" -> JsError.toJson(error).toString())) }
          },
          param => {
            val userInfo = Security.getUserinfo(request).get
            val company = userInfo.company
            val groupID = Group.withName(param.groupID)
            val newUser = buildCompanyUser(company = company,
              name = param.name,
              password = param.password,
              phone = param.phone,
              email = param.email,
              groupID = groupID)

            val f = User.newUser(newUser)
            f.onFailure(errorHandler)

            implicit val db = MongoDB.mongoClient.getDatabase(userInfo.dbName)
            val f2 =
              groupID match {
                case Group.Manager =>
                  for (storeID <- param.storeList) yield Store.addManager(storeID, newUser._id)
                case Group.Clerk =>
                  for (storeID <- param.storeList) yield Store.addClerk(storeID, newUser._id)
              }

            val f2_all = Future.sequence(f2)
            f2_all.onFailure(errorHandler)

            val overallF = Future.sequence(List(f, f2_all))
            overallF.recover({
              case _: Throwable =>
                Logger.info("recover from newUser error...")
                Ok(Json.obj("ok" -> false))
            })

            for (result <- overallF) yield {
              Ok(Json.obj("ok" -> true))
            }
          })
      })
  }

  def deleteUser(company: String, name: String) = Security.Authenticated.async {
    implicit request =>
      checkPermission(Group.allowedNewUser)({
        val f = User.deleteUser(User.buildUserID(company, name))
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

  def updateUser(id: String) = Security.Authenticated.async(BodyParsers.parse.json) {
    implicit request =>
      val userParam = request.body.validate[User]

      userParam.fold(
        error => {
          Future {
            Logger.error(JsError.toJson(error).toString())
            BadRequest(Json.obj("ok" -> false, "msg" -> JsError.toJson(error).toString()))
          }
        },
        param => {
          val f = User.updateUser(param)
          for (ret <- f) yield {
            Ok(Json.obj("ok" -> (ret.getMatchedCount == 1)))
          }
        })
  }

  def getCompanyUsers(company: String, skip: Int, limit: Int) = Security.Authenticated.async {
    val userF = User.getCompanyUsers(company)(skip, limit)
    for (users <- userF) yield Ok(Json.toJson(users))
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
        val group = users(0).groupID
        if (!allowedGroup.contains(group))
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
      val infoList = Group.getGroupInfoListBelow(Group.withName(userInfo.groupId))
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

  def getUserStoreList = Security.Authenticated.async {
    implicit request =>
      verifyUserInfo((db: MongoDatabase, usr: User) => {
        implicit val companyDB = db
        val storeListF = Store.getUserStoreList(usr)

        for (storeList <- storeListF) yield Ok(Json.toJson(storeList))
      })
  }
}
