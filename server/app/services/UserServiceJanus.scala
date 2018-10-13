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
import scala.util.Try

class UserServiceJanus @Inject()(featureService: FeatureService)
                                (implicit ec: ExecutionContext) extends UserService {

  /**
    * Find a user by its id and return the user vertex
    *
    * @param id
    * @return
    */
  private def findVertex(id: StringContainer[IdField]): Option[Vertex] = {
    Try {
      jg
        .V()
        .hasLabel(vertex.UserType)
        .has(vertex.Type, vertex.UserType)
        .has(vertex.Id, id.value)
        .next()
    }.toOption
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


  /**
    * Safe find for external use
    *
    * @param id
    * @return
    */
  def find(id: StringContainer[IdField]): Future[Option[UserModel]] = {
    Future { findVertex(id).map(v => v: UserModel) }
  }


  /**
    * Given a user id, find which group that user belongs to
    *
    * @param id
    * @return
    */
  def findGroup(id: StringContainer[IdField]): Future[Option[GroupModel]] =
    Future {

      // If the query fails return None
      val query =
        Try {
          // predicate query (should only ever be 0 or 1)
          jg
            .V()
            .has(vertex.Id, id.value)
            .inE(edge.Group2UserEdge.label)
            .toList
        }.toOption

      val vs: Option[List[Vertex]] = query.map(q => q.map(v => v.outVertex()))
      val v: Option[Vertex] = vs.flatMap(v => v.headOption)
      v.map(vm => vm: GroupModel)
    }


  /**
    * Given a user model add the user to the graph
    *
    * @param m
    * @return
    */
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

    jg.tx.commit()
    result
  }

  /**
    * Associate an EXISTING user with an EXISTING feature
    *
    * @param user
    * @param feature
    */
  def associateFeature(user: StringContainer[IdField],
                       feature: StringContainer[IdField]): Option[Vertex] = {

    for {
      featureVertex <- featureService.findVertex(feature)
      userVertex <- findVertex(user)
    } yield {
      userVertex.addEdge(edge.User2FeatureEdge.label, featureVertex)
      jg.tx.commit()
      featureVertex
    }
  }


  /**
    * Remove a given user from the graph
    *
    * @param id
    * @return
    */
  def remove(id: StringContainer[IdField]): Boolean = {
    findVertex(id)
      .map { v =>
        v.remove()
        true
      }
      .getOrElse {
        jg.tx().rollback()
        Logger.error(s"Error when attempting to remove node: ${id.value}")
        false
      }
  }

}
