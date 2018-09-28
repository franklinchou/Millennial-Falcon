package models

import ai.x.play.json.Jsonx
import lib.StringContainer
import models.fields.UserName
import org.apache.tinkerpop.gremlin.structure.Vertex


object UserModel {

  implicit lazy val jsFormat = Jsonx.formatCaseClass[UserModel]

  implicit def vertex2Model(v: Vertex): Option[UserModel] = {

    val test = ModelUtils.testVertex(v, Model.UserType)

    if (test) {
      val name = v.property(Model.Name).toString
      val model = UserModel.apply(StringContainer.apply[UserName](name))
      Some(model)
    } else {
      None
    }

  }


}

case class UserModel(name: StringContainer[UserName]) extends Model[UserName] {

  val `type`: String = Model.UserType

}
