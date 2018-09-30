package services

import com.google.inject.Inject
import dao.JanusClient.jg
import lib.StringContainer
import models.{Model, UserModel}
import models.UserModel
import models.fields.{IdField, UserField}
import org.apache.tinkerpop.gremlin.structure.Vertex
import utils.ListConversions._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class UserServiceJanus @Inject()()
                                (implicit ec: ExecutionContext) extends UserService {

  def findAllUsers: Future[List[UserModel]] =
    Future {
      jg
        .V()
        .hasLabel(Model.UserType)
        .toList
        .flatMap(v => v: Option[UserModel])
    }

  def findById(id: StringContainer[IdField]): Future[Option[UserModel]] = {

    val vertex: Option[Vertex] =
      Try {
        jg
          .V()
          .hasLabel(Model.UserType)
          .has(Model.Id, id.value)
          .next()
      }.toOption



    Future { None }
  }

  def add(m: UserModel): Vertex = {

    val createdAt = m.createdAt.toString
    val modifiedAt = m.modifiedAt.toString

    jg
      .addV(m.`type`)
      .property(Model.Type, m.`type`)
      .property(Model.Name, m.name.value)
      .property(Model.Id, m.id.value)
      .property(Model.CreatedAt, createdAt)
      .property(Model.ModifiedAt, modifiedAt)
      .next()
  }

  def remove(id: StringContainer[IdField]): Boolean = {
    try {
      jg
        .V()
        .hasLabel(Model.UserType)
        .has(Model.Id, id.value)
        .next()
        .remove()
    } catch {
      case e: Exception => false
    }
    true
  }

}
