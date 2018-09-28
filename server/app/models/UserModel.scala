package models

import java.time.ZonedDateTime

import ai.x.play.json.Jsonx
import lib.StringContainer
import models.fields.{IdField, UserField}
import org.apache.tinkerpop.gremlin.structure.Vertex


object UserModel {

  implicit lazy val jsFormat = Jsonx.formatCaseClass[UserModel]

  /**
    * Convert a string to zoned date time
    *
    * @param s
    * @return
    */
  implicit def string2ZonedDatetime(s: String): ZonedDateTime = {
    ZonedDateTime.parse(s)
  }

  /**
    * Convert a user vertex to a [[UserModel]]
    *
    * @param v
    * @return
    */
  implicit def vertex2Model(v: Vertex): Option[UserModel] = {

    val test = ModelUtils.testVertex(v, Model.UserType)

    if (test) {
      val id = v.property(Model.Id).toString
      val name = v.property(Model.Name).toString
      val createdAt: ZonedDateTime = v.property(Model.CreatedAt).toString
      val modifiedAt: ZonedDateTime = v.property(Model.ModifiedAt).toString

      val model =
        UserModel
          .apply(
            id = StringContainer.apply[IdField](id),
            name = StringContainer.apply[UserField](name),
            createdAt = createdAt,
            modifiedAt = modifiedAt
          )

      Some(model)
    } else {
      None
    }
  }


}

case class UserModel(id: StringContainer[IdField],
                     name: StringContainer[UserField],
                     createdAt: ZonedDateTime,
                     modifiedAt: ZonedDateTime) extends Model[UserField] {

  val `type`: String = Model.UserType

  def apply(name: StringContainer[UserField]): UserModel = {
    UserModel(
      id = Model.generateUUID[IdField],
      name = name,
      createdAt = Model.DefaultTime,
      modifiedAt = Model.DefaultTime
    )
  }


}
