package resources

import ai.x.play.json.Jsonx
import lib.jsonapi.Resource
import models.vertex
import models.vertex.FeatureModel
import play.api.libs.json.{JsObject, Json, OFormat}


object FeatureResource {

  implicit lazy val jsonFormat: OFormat[FeatureResource] = Jsonx.formatCaseClass[FeatureResource]

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
