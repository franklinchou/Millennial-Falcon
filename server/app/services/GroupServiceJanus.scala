package services

import com.google.inject.Inject
import dao.JanusClient.jg
import lib.StringContainer
import models.field.{IdField, UserField}
import models.vertex.{GroupModel, UserModel}
import models.{edge, vertex}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__
import org.apache.tinkerpop.gremlin.structure.Vertex
import play.api.Logger
import utils.ListConversions._

import scala.concurrent.ExecutionContext
import scala.util.Try

class GroupServiceJanus @Inject()(userService: UserService,
                                  featureService: FeatureService)
                                 (implicit ec: ExecutionContext) extends GroupService {

  /**
    * Given the group id, find the associated vertex
    *
    * @param id Group id
    * @return
    */
  def findVertex(id: StringContainer[IdField]): Option[Vertex] = {
    Try {
      jg
        .V()
        .hasLabel(vertex.GroupType)
        .has(vertex.Type, vertex.GroupType)
        .has(vertex.Id, id.value)
        .next()
    }.toOption
  }
                                   

  /**
    * Find all groups/clients
    *
    * @return
    */
  def findAllGroups: Seq[Vertex] = {
    jg
      .V()
      .hasLabel(vertex.GroupType)
      .has(vertex.Type, vertex.GroupType)
      .toList
      .toSeq
  }


  /**
    * Find all features/products associated with a given group
    *
    * @return
    */
  def findAllFeatures(groupId: StringContainer[IdField]): Seq[Vertex] = {
    findVertex(groupId)
      .map { groupVertex =>
        jg
          .V(groupVertex.id())
          .out(edge.Group2FeatureEdge.label)
          .dedup()
          .toList
          .toSeq
      }
      .getOrElse(Seq.empty[Vertex])
  }



  /**
    * Find all users associated with a group
    *
    * @param groupId
    * @return
    */
  def findAllUsers(groupId: StringContainer[IdField]): Seq[Vertex] = {
    findVertex(groupId)
      .map { group =>
        jg
          .V(group)
          .out()
          .toList
          .toSeq
      }
      .getOrElse(Seq.empty[Vertex])
  }


  // TODO Make safe
  def add(m: GroupModel): Vertex = {
    val result =
      jg
        .addV(m.`type`)
        .property(vertex.Type, m.`type`)
        .property(vertex.Name, m.name.value)
        .property(vertex.Id, m.id.value)
        .property(vertex.CreatedAt, m.createdAt.toString)
        .property(vertex.ModifiedAt, m.modifiedAt.toString)
        .next()

    val _ = jg.tx.commit()
    result
  }


  /**
    * Create a new user and associate it with a given group
    *
    * @param group Group id
    * @param name  Name of new user to create
    * @return
    */
  def associateNewUser(group: StringContainer[IdField], name: StringContainer[UserField]): Option[Vertex] = {
    val userModel = UserModel.apply(name)
    val user: Vertex = userService.add(userModel)
    findVertex(group)
      .map { g =>
        g.addEdge(edge.Group2UserEdge.label, user)
        jg.tx.commit()
        user
      }
  }


  /**
    * Associate an EXISTING user with an EXISTING group
    *
    * @param group Group id
    * @param user  User vertex
    * @return
    */
  def associateExistingUser(group: Vertex, user: Vertex): Unit = {
    group.addEdge(edge.Group2UserEdge.label, user)
    jg.tx.commit()
  }


  /**
    * Associate an EXISTING group with an EXISTING feature
    *
    * @param group
    * @param feature
    */
  def associateFeature(group: StringContainer[IdField],
                       feature: StringContainer[IdField]): Option[Vertex] =
    for {
      featureVertex <- featureService.findVertex(feature)
      groupVertex <- findVertex(group)
    } yield {
      groupVertex.addEdge(edge.Group2FeatureEdge.label, featureVertex)
      jg.tx.commit()
      featureVertex
    }


  /**
    * Dissociate a given feature from a group
    *
    * @param group
    * @param feature
    * @return
    */
  def removeFeature(group: StringContainer[IdField],
                    feature: StringContainer[IdField]): Boolean = {

    val vertices: Option[(Vertex, Vertex)] = {
      for {
        featureVertex <- featureService.findVertex(feature)
        userVertex <- findVertex(group)
        if jg.V(userVertex).out(edge.Group2FeatureEdge.label).hasId(featureVertex.id()).hasNext
      } yield (featureVertex, userVertex)
    }

    vertices
      .map { case (groupV, featureV) =>
        jg
          .V(groupV)
          .bothE()
          .where(__.otherV().is(featureV))
          .drop()
          .iterate()

        jg.tx().commit()
        true
      }
      .getOrElse {
        jg.tx().rollback()
        val message = s"Error when attempting to remove group->feature edge: ${group.value}->${feature.value}"
        Logger.error(message)
        false
      }
  }


  /**
    * Remove a user from the graph given its id
    *
    * @param id
    * @return
    */
  def remove(id: StringContainer[IdField]): Boolean = {
    findVertex(id).exists { v =>
      v.remove()
      jg.tx.commit()
      true
    }
  }

}
