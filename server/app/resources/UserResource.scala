package resources

import lib.StringContainer
import lib.jsonapi.Resource
import models.field.UserField
import models.vertex
import models.vertex.UserModel
import play.api.libs.json._

object UserResource {

  implicit lazy val reads: Reads[UserResource] = (body: JsValue) => {
    val valid =
      for {
        // TODO Abstract this to an outside JsonApi validation wrapper
        attributes <- (body \ "attributes").validate[JsObject].asOpt
        user <- (attributes \ "user").validate[String].asOpt
        if (body \ "type").validate[String].asOpt.exists(_.equals(vertex.UserType))
      } yield {
        val model = UserModel.apply(StringContainer.apply[UserField](user))
        val resource = UserResource.apply(model)
        JsSuccess(resource)
      }

    valid.getOrElse(JsError())
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
