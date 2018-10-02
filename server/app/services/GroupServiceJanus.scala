package services

import com.google.inject.Inject
import dao.JanusClient.jg
import lib.StringContainer
import models.field.IdField
import models.vertex
import models.vertex.GroupModel
import org.apache.tinkerpop.gremlin.structure.Vertex
import play.api.Logger
import utils.ListConversions._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class GroupServiceJanus @Inject()()
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
        Logger.error(s"Error when attempting to remove node: ${id.value}, $e")
        false
    }
  }

  /**
    * Create a new user and associate it with a given group
    *
    * @param id Group id
    * @return
    */
  def associateUser(id: StringContainer[IdField]): Vertex = ???

}
