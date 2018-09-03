package models.fields

import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat


/**
  * Generic user name
  */
object ModelUserName {

  implicit lazy val jsFormat: OFormat[ModelUserName] = Jsonx.formatCaseClass[ModelUserName]

}


case class ModelUserName(name: String) extends AnyVal