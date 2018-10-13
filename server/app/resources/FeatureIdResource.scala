package resources

import lib.jsonapi.Resource
import models.vertex.FeatureType
import play.api.libs.json._

object FeatureIdResource {

  implicit lazy val reads: Reads[FeatureIdResource] = (js: JsValue) => {
    val featureOpt: Option[String] =
      for {
        featureType <- (js \ "type").validate[String].asOpt
        if featureType.equals(FeatureType)
        id <- (js \ "id").validate[String].asOpt
      } yield id

    featureOpt.fold[JsResult[FeatureIdResource]](JsError())(f => JsSuccess(FeatureIdResource(f)))

  }

  implicit lazy val readList: Reads[List[FeatureIdResource]] = (js: JsValue) => {
    (js \ "data").validate[List[FeatureIdResource]](Reads.list(reads))
  }


}


case class FeatureIdResource(id: String) extends Resource {

  val `type`: String = FeatureType

  val attributes: JsObject = JsObject.empty

}