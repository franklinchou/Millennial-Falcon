package models

import lib.StringContainer
import models.fields.Feature

case class FeatureModel(feature: StringContainer[Feature]) extends Model