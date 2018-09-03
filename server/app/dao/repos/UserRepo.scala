package dao.repos

import lib.StringContainer
import models.{Model, UserModel}
import models.fields.ModelId

import scala.concurrent.Future

class UserRepo [U <: UserModel] extends GenericRepo[U] {


  def create(record: U): Future[Boolean] = ???

  def find(id: StringContainer[ModelId]): Future[Boolean] = ???

  def upsert(record: U): Future[Boolean] = ???

  def delete(id: StringContainer[ModelId]): Future[Boolean] = ???


}