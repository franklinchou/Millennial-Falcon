package resources

import lib.StringContainer
import models.field.FeatureField
import models.vertex.FeatureModel
import play.api.libs.json._

object FeatureResource {

  implicit val reads: Reads[FeatureResource] = (js: JsValue) => {
    val body = js \ "data"
    val attributesOpt = (body \ "attributes").validate[JsObject].asOpt

    // Valid JsonApi resource
    if (attributesOpt.isDefined) {
      // TODO Abstract this to a outside Resource wrapper. Maybe a Monad?
      val attributes = attributesOpt.get
      val feature = (attributes \ "feature").validate[String].get
      val model =
        FeatureModel.apply(
          StringContainer.apply[FeatureField](feature)
        )
      JsSuccess(FeatureResource(model))
    } else {
      JsError()
    }
  }

}


case class FeatureResource(model: FeatureModel)