package models.vertex

import java.time.ZonedDateTime

import lib.StringContainer
import models.field.IdField


/**
  * A generic model
  */
abstract class Model[T <: AnyVal] {

  val id: StringContainer[IdField]

  val name: StringContainer[T]

  val `type`: String

  val createdAt: ZonedDateTime

  val modifiedAt: ZonedDateTime

  lazy val modelType: String = getClass.getSimpleName

}