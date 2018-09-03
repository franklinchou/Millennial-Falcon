package models.fields

import play.api.libs.json.OFormat
import ai.x.play.json.Jsonx

object ModelId {

  implicit lazy val jsFormat: OFormat[ModelId] = Jsonx.formatCaseClass[ModelId]

}


case class ModelId(id: String) extends AnyVal
