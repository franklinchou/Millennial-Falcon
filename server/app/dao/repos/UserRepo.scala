package dao.repos

import lib.StringContainer
import models.UserModel
import models.fields.ModelId

import scala.concurrent.Future

trait UserRepo extends JanusRepo[UserModel] {

  val modelType: String = "User"

  def create(record: UserModel): Future[Boolean] = ???

  def find(id: StringContainer[ModelId]): Future[Boolean] = ???

  def delete(id: StringContainer[ModelId]): Future[Boolean] = ???

}