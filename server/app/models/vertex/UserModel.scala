package models.vertex

import java.time.ZonedDateTime

import ai.x.play.json.Jsonx
import lib.StringContainer
import models.field.{IdField, UserField}
import models.vertex
import org.apache.tinkerpop.gremlin.structure.Vertex
import play.api.libs.json.{Json, OWrites}

object UserModel {
  
  implicit lazy val jsWriter: OWrites[UserModel] = (um: UserModel) => {
    Json.obj(
      "type" -> um.`type`,
      "id" -> um.id.value,
      "attributes" -> Json.obj(
        "name" -> um.name,
        "created-at" -> um.createdAt,
        "modified-at" -> um.modifiedAt
      )
    )
  }

  /**
    * Convert a user vertex to a [[UserModel]]
    *
    * @param v
    * @return
    */
  implicit def vertex2Model(v: Vertex): UserModel = {
    val id = v.property(vertex.Id).value.toString
    val name = v.property(vertex.Name).value.toString
    val createdAt = v.property(vertex.CreatedAt).value.toString
    val modifiedAt = v.property(vertex.ModifiedAt).value.toString

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
      id = vertex.generateUUID[IdField],
      name = name,
      createdAt = vertex.DefaultTime,
      modifiedAt = vertex.DefaultTime
    )
  }

}

case class UserModel(id: StringContainer[IdField],
                     name: StringContainer[UserField],
                     createdAt: ZonedDateTime,
                     modifiedAt: ZonedDateTime) extends Model[UserField] {

  val `type`: String = vertex.UserType

}
