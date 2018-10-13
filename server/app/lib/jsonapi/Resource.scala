package lib.jsonapi

import play.api.libs.json._

object Resource {

  implicit val writes: Writes[Resource] = (resource: Resource) => {
    Json.obj(
      "id" -> resource.id,
      "type" -> resource.`type`,
      "attributes" -> resource.attributes
    )
  }

  implicit val reads: Reads[Seq[Resource]] = (json: JsValue) => {
    JsSuccess(Seq.empty[Resource])
  }

}

trait Resource {

  val `type`: String

  val id: String

  val attributes: JsObject

}
