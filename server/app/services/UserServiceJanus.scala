package services

import java.util

import com.google.inject.Inject
import dao.JanusClient.jg
import lib.StringContainer
import models.field.IdField
import models.vertex.{FeatureModel, GroupModel, UserModel}
import models.{edge, vertex}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__
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
    * Find which features this user has access to
    *
    * @param id user id
    * @return
    */
  def findFeatures(id: StringContainer[IdField]): Future[List[FeatureModel]] = {
    Future {
      findVertex(id)
        .map { userVertex =>
          jg
            .V(userVertex.id)
            .out(edge.User2FeatureEdge.label)
            .dedup()
            .toList
            .map(li => li: FeatureModel)
        }
        .getOrElse(List.empty[FeatureModel])
    }
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
    * Dissociate an EXISTING user from an EXISTING feature
    *
    * @param user
    * @param feature
    * @return
    */
  def removeFeature(user: StringContainer[IdField],
                    feature: StringContainer[IdField]): Boolean = {

    val vertices: Option[(Vertex, Vertex)] = {
      for {
        featureVertex <- featureService.findVertex(feature)
        userVertex <- findVertex(user)
        if jg.V(userVertex).out(edge.User2FeatureEdge.label).hasId(featureVertex.id()).hasNext
      } yield (featureVertex, userVertex)
    }

    vertices
      .map { case (uVertex, fVertex) =>
          jg
            .V(uVertex)
            .bothE()
            .where(__.otherV().is(fVertex))
            .drop()
            .iterate()

          jg.tx().commit()
          true
      }
      .getOrElse {
        jg.tx().rollback()
        val message = s"Error when attempting to remove user->feature edge: ${user.value}->${feature.value}"
        Logger.error(message)
        false
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
        jg.tx().commit()
        true
      }
      .getOrElse {
        jg.tx().rollback()
        Logger.error(s"Error when attempting to remove node: ${id.value}")
        false
      }
  }

}
