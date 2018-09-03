package dao.repos

import lib.StringContainer
import models.Model
import models.fields.ModelId

import scala.concurrent.Future

trait GenericRepo[T <: Model] {

  def create(record: Model): Future[Boolean]


  def find(id: StringContainer[ModelId]): Future[Boolean]


  def upsert(record: Model): Future[Boolean]


  def delete(id: StringContainer[ModelId]): Future[Boolean]

}
