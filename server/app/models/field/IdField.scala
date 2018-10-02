package models.field

import play.api.libs.json.OFormat
import ai.x.play.json.Jsonx

object IdField {

  implicit lazy val jsFormat: OFormat[IdField] = Jsonx.formatCaseClass[IdField]

}


case class IdField(id: String) extends AnyVal
