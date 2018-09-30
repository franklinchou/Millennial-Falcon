package services

import com.google.inject.Inject
import dao.JanusClient.jg
import lib.StringContainer
import models.{Model, UserModel}
import models.fields.IdField
import org.apache.tinkerpop.gremlin.structure.Vertex
import play.api.Logger
import utils.ListConversions._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class UserServiceJanus @Inject()()
                                (implicit ec: ExecutionContext) extends UserService {

  def findAllUsers: Future[List[UserModel]] =
    Future.successful {
      jg
        .V()
        .hasLabel(Model.UserType)
        .toList
        .map(v => v: UserModel)
    }

  def findById(id: StringContainer[IdField]): Future[Option[UserModel]] = {
    val model: Option[UserModel] =
      Try {
        jg
          .V()
          .hasLabel(Model.UserType)
          .has(Model.Id, id.value)
          .next()
      }.toOption.map(v => v: UserModel)

    Future { model }
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
    Try {
      jg
        .V()
        .hasLabel(Model.UserType)
        .has(Model.Id, id.value)
        .next()
        .remove()
    } match {
      case Success(_) => true
      case Failure(e) =>
        Logger.error(s"Error when attempting to remove node: ${id.value}, $e")
        false
    }
  }

}
