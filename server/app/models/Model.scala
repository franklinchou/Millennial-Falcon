package models

import java.time.{ZoneOffset, ZonedDateTime}

import lib.StringContainer
import models.fields.ModelId


/**
  * A generic model
  */
trait Model {

  val id: StringContainer[ModelId]

}



object Model {

  val DefaultTime = ZonedDateTime.now(ZoneOffset.UTC)

}