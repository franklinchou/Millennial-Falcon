package models.fields

import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat


/**
  * Generic user name
  */
object UserName {

  implicit lazy val jsFormat: OFormat[UserName] = Jsonx.formatCaseClass[UserName]

}


case class UserName(name: String) extends AnyVal