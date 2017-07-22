package controllers
import play.api._
import play.api.mvc.Security._
import play.api.mvc._
import scala.concurrent._

class AuthenticatedRequest[A](val userinfo: String, request: Request[A]) extends WrappedRequest[A](request)
case class UserInfo(id: String, name: String, groupId: String, company:String, dbName: String)

object Security {
  val idKey = "ID"
  val nameKey = "Name"
  val groupKey = "Group"
  val companyKey = "Company"
  val dbNameKey = "DbName"

  def getUserinfo(request: RequestHeader): Option[UserInfo] = {
    val idOpt = request.session.get(idKey)
    val nameOpt = request.session.get(nameKey)
    val groupOpt = request.session.get(groupKey)
    val companyOpt = request.session.get(companyKey)
    val dbNameOpt = request.session.get(dbNameKey)

    for {
      id <- idOpt
      name <- nameOpt
      groupId <- groupOpt
      company <- companyOpt
      dbName <- dbNameOpt
    } yield UserInfo(id, name, groupId, company, dbName)
  }

  def onUnauthorized(request: RequestHeader) = {
    Results.Unauthorized("Login first...")
  }

  //def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]) = {
  //  AuthenticatedBuilder(getUserinfo _, onUnauthorized)
  //})

  //def isAuthenticated(f: => String => Request[AnyContent] => Result) = {
  //  Authenticated(getUserinfo, onUnauthorized) { user =>
  //    Action(request => f(user)(request))
  //  }
  // }

  def setUserinfo[A](request: Request[A], userInfo: UserInfo) = {
    request.session +
      (idKey -> userInfo.id.toString()) + 
      (nameKey -> userInfo.name) +
      (groupKey -> userInfo.groupId) +
      (companyKey -> userInfo.company) +
      (dbNameKey -> userInfo.dbName)
  }

  def getUserInfo[A]()(implicit request: Request[A]): Option[UserInfo] = {
    getUserinfo(request)
  }

  def Authenticated = new AuthenticatedBuilder(getUserinfo, onUnauthorized)
}