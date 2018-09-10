package models

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

import lib.StringContainer
import models.fields.ModelId


/**
  * A generic model
  */
trait Model {

  val id: StringContainer[ModelId]

  lazy val modelType: String = getClass.getSimpleName

}


object Model {

  val DefaultTime = ZonedDateTime.now(ZoneOffset.UTC)

  val Id = "id"
  val Name = "name"
  val Type = "model-type"

  val UserType = "user"
  val UserGroupType = "user-group"
  val ProductType = "product"

  def generateUUID[M <: AnyVal]: StringContainer[M] = {
    val uuid = UUID.randomUUID()
    StringContainer.apply[M](uuid.toString)
  }

}