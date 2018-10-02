package models.field

import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat


/**
  * Generic user name
  */
object UserField {

  implicit lazy val jsFormat: OFormat[UserField] = Jsonx.formatCaseClass[UserField]

}


case class UserField(name: String) extends AnyVal