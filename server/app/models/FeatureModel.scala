package models

import java.time.ZonedDateTime

import lib.StringContainer
import models.fields.{FeatureField, IdField}

case class FeatureModel(id: StringContainer[IdField],
                        name: StringContainer[FeatureField],
                        createdAt: ZonedDateTime,
                        modifiedAt: ZonedDateTime) extends Model[FeatureField] {

  val `type` = Model.FeatureType

}