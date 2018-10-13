package resources

import lib.StringContainer
import lib.jsonapi.Resource
import models.field.UserField
import models.vertex
import models.vertex.UserModel
import play.api.libs.json._

object UserResource {

  implicit lazy val reads: Reads[UserResource] = (js: JsValue) => {
    val body = js \ "data"

    // TODO Abstract this to an outside JsonApi validation wrapper
    val attributes = (body \ "attributes").validate[JsObject].get

    (attributes \ "user").validate[String].fold(
      _ => JsError(),
      name => {
        val model = UserModel.apply(StringContainer.apply[UserField](name))
        val resource = UserResource.apply(model)
        JsSuccess(resource)
      }
    )
  }

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
