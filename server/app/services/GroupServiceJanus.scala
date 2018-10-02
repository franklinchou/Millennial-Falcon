package services

import com.google.inject.Inject
import dao.JanusClient.jg
import lib.StringContainer
import models.field.{IdField, UserField}
import models.vertex
import models.vertex.{GroupModel, UserModel}
import org.apache.tinkerpop.gremlin.structure.Vertex
import play.api.Logger
import utils.ListConversions._


import models.edge


import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class GroupServiceJanus @Inject()(userService: UserServiceJanus)
                                 (implicit ec: ExecutionContext) extends GroupService {

  /**
    * Given the group id, find the associated vertex
    *
    * @param id Group id
    * @return
    */
  private def findById(id: StringContainer[IdField]): Vertex = {
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


  def find(id: StringContainer[IdField]): Future[Option[GroupModel]] =
    Future {
      Try {
        findById(id)
      }.toOption.map(v => v: GroupModel)
    }


  def add(m: GroupModel): Vertex =
    jg
      .addV(m.`type`)
      .property(vertex.Type, m.`type`)
      .property(vertex.Name, m.name.value)
      .property(vertex.Id, m.id.value)
      .property(vertex.CreatedAt, m.createdAt.toString)
      .property(vertex.ModifiedAt, m.modifiedAt.toString)
      .next()

  def remove(id: StringContainer[IdField]): Boolean = {
    Try {
      findById(id).remove()
    } match {
      case Success(_) => true
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
        user.addEdge(edge.User2FeatureEdge.label, findById(group))
        Some(user)
      case Failure(e) =>
        Logger.error(s"Error when attempting to find group, $e")
        None
    }
  }

}
