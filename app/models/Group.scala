package models
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json

case class GroupInfo(id: String, name: String)
object Group extends Enumeration {
  val Admin = Value
  val Owner = Value
  val Manager = Value
  val Clerk = Value

  val levelSeq = Seq(Owner, Manager, Clerk) 
  val map = Map(
    Admin -> "系統管理員",
    Owner -> "老闆",
    Manager -> "店長",
    Clerk -> "店員")

  def getInfoList ={
    map.map { m => GroupInfo(m._1.toString, m._2) }.toList
  }

  def getGroupInfoListBelow(level:Group.Value) ={
    val seq = levelSeq.dropWhile { x => x != level }
    map.filterKeys { key => seq.contains(key)  }.map { m => GroupInfo(m._1.toString, m._2) }.toList
  }

  val allowedNewUser = Seq(Group.Admin, Group.Owner, Group.Manager)
  val allowedDelUser = Seq(Group.Admin, Group.Owner, Group.Manager)

  implicit val reads: Reads[Group.Value] = EnumUtils.enumReads(Group)
  implicit val writes: Writes[Group.Value] = EnumUtils.enumWrites
}