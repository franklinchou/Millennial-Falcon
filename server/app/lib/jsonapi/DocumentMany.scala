package lib.jsonapi

import ai.x.play.json.Jsonx
import play.api.libs.json.{JsObject, OFormat}

object DocumentMany {

  implicit lazy val jsonFormat: OFormat[DocumentMany] = Jsonx.formatCaseClass[DocumentMany]

}


// TODO Make included type Seq[Resource]
case class DocumentMany(data: Seq[Resource], included: Seq[JsObject], meta: JsObject) extends Document