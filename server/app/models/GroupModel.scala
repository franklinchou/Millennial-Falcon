package models

import java.time.ZonedDateTime

import ai.x.play.json.Jsonx
import lib.StringContainer
import models.fields.{GroupField, IdField}
import org.apache.tinkerpop.gremlin.structure.Vertex

object GroupModel {

  implicit lazy val jsFormat = Jsonx.formatCaseClass[GroupModel]

  /**
    * Convert a group vertex to a [[GroupModel]]
    *
    * @param v
    * @return
    */
  implicit def vertex2Model(v: Vertex): GroupModel = {
    val id = v.property(Model.Id).value.toString
    val group = v.property(Model.Name).value.toString
    val createdAt = v.property(Model.CreatedAt).value.toString
    val modifiedAt = v.property(Model.ModifiedAt).value.toString

    GroupModel
      .apply(
        id = StringContainer.apply[IdField](id),
        name = StringContainer.apply[GroupField](group),
        createdAt = ZonedDateTime.parse(createdAt),
        modifiedAt = ZonedDateTime.parse(modifiedAt)
      )
  }


  /**
    * Create a [[UserModel]]
    *
    * @param name Group/Client name
    * @return
    */
  def apply(name: StringContainer[GroupField]): GroupModel = {
    GroupModel(
      id = Model.generateUUID[IdField],
      name = name,
      createdAt = Model.DefaultTime,
      modifiedAt = Model.DefaultTime
    )
  }

}


case class GroupModel(id: StringContainer[IdField],
                      name: StringContainer[GroupField],
                      createdAt: ZonedDateTime,
                      modifiedAt: ZonedDateTime) extends Model[GroupField] {

  val `type`: String = Model.GroupType

}

