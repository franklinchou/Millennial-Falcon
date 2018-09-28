package models

import ai.x.play.json.Jsonx
import lib.StringContainer
import models.fields.ModelUserName
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex


object UserModel {

  implicit lazy val jsFormat = Jsonx.formatCaseClass[UserModel]

  implicit def userVertex2Model(v: Vertex): Option[UserModel] = {

    if (ModelUtils.testVertex(v, Model.UserType)) {
      // val id = v.property(Model.Name).toString
      val name = v.property(Model.Id).toString

      val model = UserModel.apply(StringContainer.apply[ModelUserName](name))

      Some(model)

    } else {
      None
    }
  }

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


case class UserModel(userName: StringContainer[ModelUserName]) extends Model