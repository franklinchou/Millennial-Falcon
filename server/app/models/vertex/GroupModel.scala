package models.vertex

import java.time.ZonedDateTime

import lib.StringContainer
import models.field.{GroupField, IdField}
import models.vertex
import org.apache.tinkerpop.gremlin.structure.Vertex
import play.api.libs.json.{Json, OWrites}

object GroupModel {

  implicit lazy val jsWriter: OWrites[GroupModel] = (gm: GroupModel) => {
    Json.obj(
      "type" -> gm.`type`,
      "id" -> gm.id.value,
      "attributes" -> Json.obj(
        "group" -> gm.name,
        "created-at" -> gm.createdAt.toString,
        "modified-at" -> gm.modifiedAt.toString
      )
    )
  }


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
        id = StringContainer.apply[IdField](""),
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

