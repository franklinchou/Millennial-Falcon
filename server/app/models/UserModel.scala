package models

import java.time.ZonedDateTime

import ai.x.play.json.Jsonx
import lib.StringContainer
import models.Model.DefaultTime
import models.fields.{ModelId, ModelUserName}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex


object UserModel {

  implicit lazy val jsFormat = Jsonx.formatCaseClass[UserModel]

  val `type`: String = Model.UserType

  def add(um: UserModel, jg: GraphTraversalSource): Vertex = {
    jg
      .addV(`type`)
      .property(Model.Name, um.userName.value)
      .property(Model.Id, um.id.value)
      .property(Model.Type, `type`)
      .next()
  }

}


case class UserModel(id: StringContainer[ModelId] = Model.generateUUID[ModelId],
                     userName: StringContainer[ModelUserName],
                     createdAt: ZonedDateTime = DefaultTime,
                     updatedAt: ZonedDateTime = DefaultTime) extends Model