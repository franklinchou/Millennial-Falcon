package models

import lib.StringContainer
import models.fields.FeatureName

case class FeatureModel(name: StringContainer[FeatureName]) extends Model[FeatureName] {

  val `type` = Model.FeatureType

}