package models.fields

import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat

/**
  * Generic group name
  */
object GroupName {

  implicit lazy val jsFormat: OFormat[GroupName] = Jsonx.formatCaseClass[GroupName]

}


case class GroupName(name: String) extends AnyVal