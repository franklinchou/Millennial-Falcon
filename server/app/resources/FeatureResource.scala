package resources

import lib.StringContainer
import lib.jsonapi.Resource
import models.field.FeatureField
import models.vertex
import models.vertex.FeatureModel
import play.api.libs.json._


object FeatureResource {

  implicit lazy val reads: Reads[FeatureResource] = (js: JsValue) => {
    val body = js \ "data"

    // TODO Abstract this to an outside JsonApi validation wrapper
    val attributes = (body \ "attributes").validate[JsObject].get

    (attributes \ "feature").validate[String].fold(
      _ => JsError(),
      feature => {
        val model = FeatureModel.apply(StringContainer.apply[FeatureField](feature))
        val resource = FeatureResource.apply(model)
        JsSuccess(resource)
      }
    )
  }
}


case class FeatureResource(featureModel: FeatureModel) extends Resource {

  val `type`: String = vertex.FeatureType

  val id: String = featureModel.id.value

  lazy val attributes: JsObject =
    Json.obj(
      "feature" -> featureModel.name,
      "created-at" -> featureModel.createdAt,
      "modified-at" -> featureModel.modifiedAt
    )

}
