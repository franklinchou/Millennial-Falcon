package resources

import lib.StringContainer
import lib.jsonapi.Resource
import models.field.GroupField
import models.vertex
import models.vertex.GroupModel
import play.api.libs.json._

object GroupResource {

  // TODO Abstract this implicit reads function across all resources
  implicit lazy val reads: Reads[GroupResource] = (js: JsValue) => {
    val body = js \ "data"

    // TODO Abstract this to an outside JsonApi validation wrapper
    val attributes = (body \ "attributes").validate[JsObject].get

    // TODO How to enforce type?
    (attributes \ "group").validate[String].fold(
      _ => JsError(),
      group => {
        val model = GroupModel.apply(StringContainer.apply[GroupField](group))
        val resource = GroupResource.apply(model)
        JsSuccess(resource)
      }
    )
  }
}

case class GroupResource(groupModel: GroupModel) extends Resource {

  val `type`: String = vertex.GroupType

  lazy val id: String = groupModel.id.value

  lazy val attributes: JsObject =
    Json.obj(
      "group" -> groupModel.name,
      "created-at" -> groupModel.createdAt,
      "modified-at" -> groupModel.modifiedAt
    )

}
