package models

import java.time.ZonedDateTime

import ai.x.play.json.Jsonx
import lib.StringContainer
import models.fields.{IdField, UserField}
import org.apache.tinkerpop.gremlin.structure.Vertex

object UserModel {

  implicit lazy val jsFormat = Jsonx.formatCaseClass[UserModel]

  /**
    * Convert a user vertex to a [[UserModel]]
    *
    * @param v
    * @return
    */
  implicit def vertex2Model(v: Vertex): UserModel = {
    val id = v.property(models.Id).value.toString
    val name = v.property(models.Name).value.toString
    val createdAt = v.property(models.CreatedAt).value.toString
    val modifiedAt = v.property(models.ModifiedAt).value.toString

    UserModel
      .apply(
        id = StringContainer.apply[IdField](id),
        name = StringContainer.apply[UserField](name),
        createdAt = ZonedDateTime.parse(createdAt),
        modifiedAt = ZonedDateTime.parse(modifiedAt)
      )
  }


  def apply(name: StringContainer[UserField]): UserModel = {
    UserModel(
      id = models.generateUUID[IdField],
      name = name,
      createdAt = models.DefaultTime,
      modifiedAt = models.DefaultTime
    )
  }

}

case class UserModel(id: StringContainer[IdField],
                     name: StringContainer[UserField],
                     createdAt: ZonedDateTime,
                     modifiedAt: ZonedDateTime) extends Model[UserField] {

  val `type`: String = models.UserType

}
