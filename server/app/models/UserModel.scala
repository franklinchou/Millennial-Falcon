package models

import java.time.ZonedDateTime

import ai.x.play.json.Jsonx
import lib.StringContainer
import models.Model.DefaultTime
import models.fields.{ModelId, ModelUserName}


object UserModel {

  implicit lazy val jsFormat = Jsonx.formatCaseClass[UserModel]

}


case class UserModel(id: StringContainer[ModelId] = Model.generateUUID[ModelId],
                     userName: StringContainer[ModelUserName],
                     createdAt: ZonedDateTime = DefaultTime,
                     updatedAt: ZonedDateTime = DefaultTime) extends Model