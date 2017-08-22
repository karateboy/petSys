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
import scala.collection.JavaConversions._

case class Breed(_id: String)
object Breed {
  import org.mongodb.scala._
  import org.mongodb.scala.bson.codecs.Macros._
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.bson.codecs.configuration.CodecRegistries.{ fromRegistries, fromProviders }

  val colName = "breed"
  val codecRegistry = fromRegistries(
    fromProviders(classOf[Breed]), DEFAULT_CODEC_REGISTRY)

  def collection(implicit db: MongoDatabase) =
    db.getCollection[Breed](colName).withCodecRegistry(codecRegistry)
  
  val defaultBreedListOpt = Play.current.configuration.getStringList("default.breedList")
  val defaultBreedList = if (defaultBreedListOpt.isEmpty)
    List.empty[String]
  else
    defaultBreedListOpt.get.toList

  def init(colNames: Seq[String])(implicit db: MongoDatabase) {
    if (!colNames.contains(colName)) {
      val f = db.createCollection(colName).toFuture()
      f.onFailure(errorHandler)
      f.onSuccess({
        case x: Completed =>
          val breedList = defaultBreedList map { Breed(_) }
          val f2 = collection.insertMany(breedList).toFuture()
          f2.onFailure(errorHandler)
      })
    }
  }

  def newBreed(breed: Breed)(implicit db: MongoDatabase) = {
    val f = collection.insertOne(breed).toFuture()
    f.onFailure(errorHandler)
    f
  }

  def getBreedList(implicit db: MongoDatabase) = {
    val f = collection.find().toFuture()
    f.onFailure(errorHandler)
    for (seq <- f)
      yield seq
  }
}