package models

import lib.StringContainer
import models.fields.{Feature, ModelId}

case class FeatureModel(id: StringContainer[ModelId] = Model.generateUUID[ModelId],
                        feature: StringContainer[Feature]) extends Model