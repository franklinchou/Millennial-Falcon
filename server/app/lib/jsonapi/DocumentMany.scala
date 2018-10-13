package lib.jsonapi

import play.api.libs.json.{JsObject, Json, OWrites}

object DocumentMany {

  implicit lazy val jsonFormat: OWrites[DocumentMany] = Json.writes[DocumentMany]

}


case class DocumentMany(data: Seq[Resource], included: Seq[JsObject], meta: JsObject) extends Document