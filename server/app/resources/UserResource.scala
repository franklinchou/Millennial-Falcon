package resources

import ai.x.play.json.Jsonx
import lib.jsonapi.Resource
import models.vertex
import models.vertex.UserModel
import play.api.libs.json.{JsObject, Json, OFormat}

object UserResource {

  implicit lazy val jsonFormat: OFormat[UserResource] = Jsonx.formatCaseClass[UserResource]

}


case class UserResource(userModel: UserModel) extends Resource {

  val `type`: String = vertex.UserType

  lazy val id: String = userModel.id.value

  lazy val attributes: JsObject =
    Json.obj(
      "user" -> userModel.name,
      "created-at" -> userModel.createdAt,
      "modified-at" -> userModel.modifiedAt
    )

}
