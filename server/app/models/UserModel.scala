package models

import ai.x.play.json.Jsonx
import lib.StringContainer
import models.fields.UserName


object UserModel {

  implicit lazy val jsFormat = Jsonx.formatCaseClass[UserModel]

}

case class UserModel(name: StringContainer[UserName]) extends Model[UserName] {

  val `type`: String = Model.UserType

}
