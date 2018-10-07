package services

import com.google.inject.Inject
import dao.JanusClient.jg
import lib.StringContainer
import models.field.IdField
import models.vertex.{GroupModel, UserModel}
import models.{edge, vertex}
import org.apache.tinkerpop.gremlin.structure.Vertex
import play.api.Logger
import utils.ListConversions._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class UserServiceJanus @Inject()()
                                (implicit ec: ExecutionContext) extends UserService {

  /**
    * Find a user by its id and return the user vertex
    *
    * @param id
    * @return
    */
  private def find(id: StringContainer[IdField]): Vertex = {
    jg
      .V()
      .hasLabel(vertex.UserType)
      .has(vertex.Type, vertex.UserType)
      .has(vertex.Id, id.value)
      .next()
  }

  def findAllUsers: Future[List[UserModel]] =
    Future.successful {
      jg
        .V()
        .hasLabel(vertex.UserType)
        .has(vertex.Type, vertex.UserType)
        .toList
        .map(v => v: UserModel)
    }


  def findById(id: StringContainer[IdField]): Future[Option[UserModel]] = {
    Future {
      Try {
        find(id)
      }.toOption.map(v => v: UserModel)
    }
  }


  /**
    * Given a user id, find which group that user belongs to
    *
    * @param id
    * @return
    */
  def findGroup(id: StringContainer[IdField]): Future[Option[GroupModel]] =
    Future {

      // predicate query (should only ever be 0 or 1)
      jg
        .V()
        .has(vertex.Id, id.value)
        .inE(edge.Group2UserEdge.label)
        .toList
        .headOption
        .map(v => v.outVertex())
        .map(c => c: GroupModel)
    }


  def add(m: UserModel): Vertex = {

    val createdAt = m.createdAt.toString
    val modifiedAt = m.modifiedAt.toString

    val result =
      jg
        .addV(m.`type`)
        .property(vertex.Type, m.`type`)
        .property(vertex.Name, m.name.value)
        .property(vertex.Id, m.id.value)
        .property(vertex.CreatedAt, createdAt)
        .property(vertex.ModifiedAt, modifiedAt)
        .next()

    val _ = jg.tx.commit()
    result
  }

  def remove(id: StringContainer[IdField]): Boolean = {
    Try {
      find(id).remove()
    } match {
      case Success(_) =>
        jg.tx.commit()
        true
      case Failure(e) =>
        val _ = jg.tx().rollback()
        Logger.error(s"Error when attempting to remove node: ${id.value}, $e")
        false
    }
  }

}
