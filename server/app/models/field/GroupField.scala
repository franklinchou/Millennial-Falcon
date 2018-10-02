package models.field

import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat

/**
  * Generic group name
  */
object GroupField {

  implicit lazy val jsFormat: OFormat[GroupField] = Jsonx.formatCaseClass[GroupField]

}


case class GroupField(name: String) extends AnyVal