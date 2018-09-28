package models

import lib.StringContainer
import models.fields.FeatureField

case class FeatureModel(name: StringContainer[FeatureField]) extends Model[FeatureField] {

  val `type` = Model.FeatureType

}