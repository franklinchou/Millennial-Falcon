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
  def findById(id: StringContainer[IdField]): Vertex = {
    jg
      .V()
      .hasLabel(vertex.GroupType)
      .has(vertex.Type, vertex.GroupType)
      .has(vertex.Id, id.value)
      .next()
  }

  /**
    * Find all groups/clients
    *
    * @return
    */
  def findAllGroups: Future[List[GroupModel]] = {
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
        Future { List.empty[GroupModel] }
    }
  }

  /**
    * Find all users associated with a group
    *
    * @param groupId
    * @return
    */
  def findAllUsers(groupId: StringContainer[IdField]): Future[List[UserModel]] = {
    Try {
      val groupVertex = findById(groupId)
      jg
        .V(groupVertex.id)
        .out()
        .toList
        .map(v => v: UserModel)
    } match {
      case Success(users) => Future { users }
      case Failure(e) =>
        Logger.error(s"`findAllUsers` failed with error $e")
        Future { List.empty[UserModel] }
    }
  }


  def find(id: StringContainer[IdField]): Future[Option[GroupModel]] =
    Future {
      Try {
        findById(id)
      }.toOption.map(v => v: GroupModel)
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
    * Remove a user from the graph given its id
    *
    * @param id
    * @return
    */
  def remove(id: StringContainer[IdField]): Boolean = {
    Try {
      findById(id).remove()
    } match {
      case Success(_) =>
        val _ = jg.tx.commit()
        true
      case Failure(e) =>
        val _ = jg.tx().rollback()
        Logger.error(s"Error when attempting to remove group: ${id.value}, $e")
        false
    }
  }

  /**
    * Create a new user and associate it with a given group
    *
    * @param group Group id
    * @param name  Name of new user to create
    * @return
    */
  def associateUser(group: StringContainer[IdField], name: StringContainer[UserField]): Option[Vertex] = {
    val userModel = UserModel.apply(name)
    val user: Vertex = userService.add(userModel)

    Try {
      findById(group)
    } match {
      case Success(g) =>
        findById(group).addEdge(edge.Group2UserEdge.label, user)
        // user.addEdge(edge.Group2UserEdge.label, findById(group))
        val _ = jg.tx.commit()
        Some(user)
      case Failure(e) =>
        Logger.error(s"Error when attempting to find group, $e")
        None
    }
  }

}
