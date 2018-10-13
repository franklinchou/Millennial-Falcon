package resources

import ai.x.play.json.Jsonx
import lib.jsonapi.Resource
import models.vertex
import models.vertex.GroupModel
import play.api.libs.json.{JsObject, Json, OFormat}

object GroupResource {

  implicit lazy val jsonFormat: OFormat[GroupResource] = Jsonx.formatCaseClass[GroupResource]

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
