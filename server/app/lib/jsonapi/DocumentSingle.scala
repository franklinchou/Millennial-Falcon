package lib.jsonapi

import play.api.libs.json.{Json, OWrites}

object DocumentSingle {

  implicit lazy val jsonFormat: OWrites[DocumentSingle] = Json.writes[DocumentSingle]

}


case class DocumentSingle(data: Resource, included: Seq[Resource]) extends Document