package lib.jsonapi

import play.api.libs.json.{JsObject, Json, Writes}

object Resource {

  implicit val writes: Writes[Resource] = (resource: Resource) => {
    Json.obj(
      "id" -> resource.id,
      "type" -> resource.`type`,
      "attributes" -> resource.attributes
    )
  }

}

trait Resource {

  val `type`: String

  val id: String

  val attributes: JsObject

}
