package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger
import models._
import models.ModelHelper._
import scala.concurrent.ExecutionContext.Implicits.global

case class Credential(company: String, name: String, password: String)

/**
 * @author user
 */
object Login extends Controller {
  implicit val credentialReads = Json.reads[Credential]

  def getUserCredential(user: User) = {
    val companyOpt = waitReadyResult(Company.findCompany(user.company))
    if (companyOpt.isEmpty)
      throw new Exception(s"Company ${user.company} not existed!")

    val company = companyOpt.get
    val userInfo = UserInfo(user._id, user.name, user.groupID.toString(), company._id, company.dbName)
    val storeListF = Store.getUserStoreList(user)(company.database)
    val storeList = waitReadyResult(storeListF)
    (Json.obj("ok" -> true, "user" -> user, "storeList" -> storeList), userInfo)
  }

  def authenticate = Action(BodyParsers.parse.json) {
    implicit request =>
      val credentail = request.body.validate[Credential]
      credentail.fold(
        error => {
          BadRequest(Json.obj("ok" -> false, "msg" -> JsError.toJson(error)))
        },
        crd => {
          val usersF = User.getUserByCompanyAndName(crd.company, crd.name)
          val users = ModelHelper.waitReadyResult(usersF)
          if (users.isEmpty || users(0).password != crd.password)
            Ok(Json.obj("ok" -> false, "msg" -> "密碼或帳戶錯誤"))
          else {
            val (ret, userInfo) = getUserCredential(users(0))
            Ok(ret).withSession(Security.setUserinfo(request, userInfo))
          }
        })
  }

  def testAuthenticated = Security.Authenticated {
    implicit request =>
      val userInfo = request.user
      val userF = User.getUserByCompanyAndName(userInfo.company, userInfo.name)
      val users = ModelHelper.waitReadyResult(userF)
      if (users.length == 0)
        Ok(Json.obj("ok" -> false, "msg" -> "帳戶不存在")).withNewSession
      else {
        val (ret, userInfo) = getUserCredential(users(0))
        Ok(ret).withSession(Security.setUserinfo(request, userInfo))
      }
  }

  def logout = Action {
    Ok(Json.obj(("ok" -> true))).withNewSession
  }
}