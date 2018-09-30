package models

import java.time.ZonedDateTime

import lib.StringContainer
import models.fields.{FeatureField, IdField}
import org.apache.tinkerpop.gremlin.structure.Vertex

case class FeatureModel(id: StringContainer[IdField],
                        name: StringContainer[FeatureField],
                        createdAt: ZonedDateTime,
                        modifiedAt: ZonedDateTime) extends Model[FeatureField] {

  val `type` = Model.FeatureType

  def add(m: Model[FeatureField]): Vertex = ???
}