package models

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

import lib.StringContainer
import models.Model.DefaultTime
import models.fields.ModelId
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex


/**
  * A generic model
  */
trait Model[T <: AnyVal] {

  val id: StringContainer[ModelId] = Model.generateUUID[ModelId]

  val name: StringContainer[T]

  val `type`: String

  val createdAt: ZonedDateTime = DefaultTime

  val updatedAt: ZonedDateTime = DefaultTime

  lazy val modelType: String = getClass.getSimpleName

}


object Model {

  val DefaultTime: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)

  val Id = "id"
  val Name = "name"
  val Type = "model-type"
  val CreatedAt = "created-at"
  val UpdatedAt = "updated-at"

  val UserType = "user"
  val GroupType = "group"
  val FeatureType = "product"

  def generateUUID[M <: AnyVal]: StringContainer[M] = {
    val uuid = UUID.randomUUID()
    StringContainer.apply[M](uuid.toString)
  }


  /**
    *
    * @param vs
    * @param ev
    * @tparam A
    * @tparam B
    * @tparam T
    * @return
    */
  implicit def mapList[A, B <: Model[T], T <: AnyVal](vs: List[A])
                                                     (implicit ev: A => Option[B]): List[B] = {

    vs.flatMap(v => v: Option[B])
  }


  def add[T <: AnyVal](m: Model[T], jg: GraphTraversalSource): Vertex = {
    jg
      .addV(m.`type`)
      .property(Model.Name, m.name.value)
      .property(Model.Id, m.id.value)
      .property(Model.Type, m.`type`)
      .property(Model.CreatedAt, m.createdAt.toString)
      .property(Model.UpdatedAt, m.updatedAt.toString)
      .next()
  }

}