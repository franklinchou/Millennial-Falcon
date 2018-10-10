package models.vertex

import java.time.ZonedDateTime

import lib.StringContainer
import models.field.{FeatureField, IdField}
import models.vertex
import org.apache.tinkerpop.gremlin.structure.Vertex

case class FeatureModel(id: StringContainer[IdField],
                        name: StringContainer[FeatureField],
                        createdAt: ZonedDateTime,
                        modifiedAt: ZonedDateTime) extends Model[FeatureField] {

  val `type` = vertex.FeatureType

  def add(m: Model[FeatureField]): Vertex = ???

}