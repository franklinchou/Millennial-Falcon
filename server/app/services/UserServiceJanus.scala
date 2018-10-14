package services

import com.google.inject.Inject
import dao.JanusClient.jg
import lib.StringContainer
import models.field.IdField
import models.vertex.{FeatureModel, UserModel}
import models.{edge, vertex}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__
import org.apache.tinkerpop.gremlin.structure.Vertex
import play.api.Logger
import utils.ListConversions._

import scala.concurrent.ExecutionContext
import scala.util.Try

class UserServiceJanus @Inject()(featureService: FeatureService)
                                (implicit ec: ExecutionContext) extends UserService {

  /**
    * Find a user by its id and return the user vertex
    *
    * @param id
    * @return
    */
  def findUserVertex(id: StringContainer[IdField]): Option[Vertex] = {
    Try {
      jg
        .V()
        .hasLabel(vertex.UserType)
        .has(vertex.Type, vertex.UserType)
        .has(vertex.Id, id.value)
        .next()
    }.toOption
  }
                                  

  /**
    * Find the group associated with a user and return the group vertex
    *
    * @param id
    * @return
    */
  def findGroupVertexByUser(id: StringContainer[IdField]): Option[Vertex] = {
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
    vs.flatMap(v => v.headOption)
  }


  def findAllUsers: Seq[Vertex] =
    jg
      .V()
      .hasLabel(vertex.UserType)
      .has(vertex.Type, vertex.UserType)
      .toList
      .toSeq



  /**
    * Find which features this user has access to
    *
    * @param id user id
    * @return
    */
  def findFeatures(id: StringContainer[IdField]): Seq[Vertex] = {
    findUserVertex(id)
      .map { userVertex =>
        jg
          .V(userVertex.id)
          .out(edge.User2FeatureEdge.label)
          .dedup()
          .toList
          .toSeq
      }
      .getOrElse(Seq.empty[Vertex])
  }


  /**
    * Given a user model, add the user to the graph
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
      userVertex <- findUserVertex(user)
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
        userVertex <- findUserVertex(user)
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
    * Dissociate an EXISTING user from its group
    * There should only ever be one group!
    *
    * @param user
    * @return
    */
  def removeGroup(user: StringContainer[IdField]): Boolean = {

    val vertices: Option[(Vertex, Vertex)] = {
      for {
        userVertex <- findUserVertex(user)
        groupVertex <- findGroupVertexByUser(user)
      } yield (userVertex, groupVertex)
    }

    vertices
      .map { case (uVertex, gVertex) =>
        jg
          .V(uVertex)
          .bothE()
          .where(__.otherV().is(gVertex))
          .drop()
          .iterate()

        jg.tx().commit()
        true
      }
      .getOrElse {
        jg.tx().rollback()
        val message = s"Error when attempting to remove user->group edge"
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
    findUserVertex(id)
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
