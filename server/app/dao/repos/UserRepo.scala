package dao.repos

import lib.StringContainer
import models.UserModel
import models.fields.ModelId

import scala.concurrent.Future

class UserRepo extends JanusRepo[UserModel] {

  def create(record: UserModel): Future[Boolean] = ???

  def find(id: StringContainer[ModelId]): Future[Boolean] = ???

  def upsert(record: UserModel): Future[Boolean] = ???

  def delete(id: StringContainer[ModelId]): Future[Boolean] = ???

}