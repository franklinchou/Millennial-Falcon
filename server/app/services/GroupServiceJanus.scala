package services

import com.google.inject.Inject
import dao.JanusClient.jg
import lib.StringContainer
import models.field.{IdField, UserField}
import models.vertex.{GroupModel, UserModel}
import models.{edge, vertex}
import org.apache.tinkerpop.gremlin.structure.Vertex
import play.api.Logger
import utils.ListConversions._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class GroupServiceJanus @Inject()(userService: UserService)
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
    * Safe find for external use.
    *
    * @param id
    * @return
    */
  def find(id: StringContainer[IdField]): Future[Option[GroupModel]] =
    Future { findVertex(id).map(v => v: GroupModel) }


  /**
    * Find all groups/clients
    *
    * @return
    */
  def findAllGroups: Future[Seq[GroupModel]] = {
    Try {
      jg
        .V()
        .hasLabel(vertex.GroupType)
        .has(vertex.Type, vertex.GroupType)
        .toList
        .map(v => v: GroupModel)
    } match {
      case Success(groups) => Future { groups }
      case Failure(e) =>
        Logger.error(s"`findAllGroups` failed with error $e")
        Future { Seq.empty[GroupModel] }
    }
  }

  /**
    * Find all users associated with a group
    *
    * @param groupId
    * @return
    */
  def findAllUsers(groupId: StringContainer[IdField]): Future[List[UserModel]] = {
    Future {
      findVertex(groupId).map { group =>
        jg
          .V(group)
          .out()
          .toList
          .map(v => v: UserModel)
      }.getOrElse(List.empty[UserModel])
    }
  }


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
