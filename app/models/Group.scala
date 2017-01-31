package models

case class GroupInfo(id:String, name:String)
object Group extends Enumeration {
  val Admin = Value
  val Owner = Value
  val Manager = Value
  val Clerk = Value

  val map = Map(
    Admin -> "系統管理員",
    Owner->"老闆",
    Manager->"經理",
    Clerk->"店員")
    
  def getInfoList = map.map {m => GroupInfo(m._1.toString, m._2)}.toList  
}