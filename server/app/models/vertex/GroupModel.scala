package models.vertex

import java.time.ZonedDateTime

import ai.x.play.json.Jsonx
import lib.StringContainer
import models.field.{GroupField, IdField}
import models.vertex
import org.apache.tinkerpop.gremlin.structure.Vertex
import play.api.libs.json.OFormat

object GroupModel {


  implicit lazy val jsonFormat: OFormat[GroupModel] = Jsonx.formatCaseClass[GroupModel]


  /**
    * Convert a group vertex to a [[GroupModel]]
    *
    * @param v
    * @return
    */
  implicit def vertex2Model(v: Vertex): GroupModel = {
    val id = v.property(vertex.Id).value.toString
    val group = v.property(vertex.Name).value.toString
    val createdAt = v.property(vertex.CreatedAt).value.toString
    val modifiedAt = v.property(vertex.ModifiedAt).value.toString

    GroupModel
      .apply(
        id = StringContainer.apply[IdField](id),
        name = StringContainer.apply[GroupField](group),
        createdAt = ZonedDateTime.parse(createdAt),
        modifiedAt = ZonedDateTime.parse(modifiedAt)
      )
  }


  /**
    * Create a [[GroupModel]]
    *
    * @param group Group/Client name
    * @return
    */
  def apply(group: StringContainer[GroupField]): GroupModel = {
    GroupModel(
      id = vertex.generateUUID[IdField],
      name = group,
      createdAt = vertex.DefaultTime,
      modifiedAt = vertex.DefaultTime
    )
  }

}


case class GroupModel(id: StringContainer[IdField],
                      name: StringContainer[GroupField],
                      createdAt: ZonedDateTime,
                      modifiedAt: ZonedDateTime) extends Model[GroupField] {

  val `type`: String = vertex.GroupType

}

