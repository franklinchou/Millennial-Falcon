package models

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

import lib.StringContainer
import models.fields.IdField
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex

object Model {

  val DefaultTime: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)

  val Id = "id"
  val Name = "name"
  val Type = "model-type"
  val CreatedAt = "created-at"
  val ModifiedAt = "modified-at"

  val UserType = "user"
  val GroupType = "group"
  val FeatureType = "product"

  def generateUUID[M <: AnyVal]: StringContainer[M] = {
    val uuid = UUID.randomUUID()
    StringContainer.apply[M](uuid.toString)
  }

  def add[T <: AnyVal](m: Model[T], jg: GraphTraversalSource): Vertex = {

    val createdAt = m.createdAt.toString
    val modifiedAt = m.modifiedAt.toString

    jg
      .addV(m.`type`)
      .property(Model.Type, m.`type`)
      .property(Model.Name, m.name.value)
      .property(Model.Id, m.id.value)
      .property(Model.CreatedAt, createdAt)
      .property(Model.ModifiedAt, modifiedAt)
      .next()
  }

}


/**
  * A generic model
  */
trait Model[T <: AnyVal] {

  val id: StringContainer[IdField]

  val name: StringContainer[T]

  val `type`: String

  val createdAt: ZonedDateTime

  val modifiedAt: ZonedDateTime

  lazy val modelType: String = getClass.getSimpleName

}