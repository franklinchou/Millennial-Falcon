package lib.jsonapi

import play.api.libs.json.{JsObject, Json, OWrites}

object DocumentSingle {

  implicit lazy val jsonFormat: OWrites[DocumentSingle] = Json.writes[DocumentSingle]

}


case class DocumentSingle(data: Resource, included: Seq[JsObject]) extends Document