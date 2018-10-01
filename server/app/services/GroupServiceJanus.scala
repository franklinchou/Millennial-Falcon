package services

import com.google.inject.Inject
import dao.JanusClient.jg
import lib.StringContainer
import models.fields.IdField
import models.{GroupModel, Model}
import org.apache.tinkerpop.gremlin.structure.Vertex
import play.api.Logger
import utils.ListConversions._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class GroupServiceJanus @Inject()()
                                 (implicit ec: ExecutionContext) extends GroupService {

  /**
    * Find all groups/clients
    *
    * @return
    */
  def findAllGroups: Future[List[GroupModel]] =
    Future.successful {
      jg
        .V()
        .hasLabel(Model.GroupType)
        .has(Model.Type, Model.GroupType)
        .toList
        .map(v => v:GroupModel)
    }

  def findById(id: StringContainer[IdField]): Future[Option[GroupModel]] = {
    val model: Option[GroupModel] =
      Try {
        jg
          .V()
          .hasLabel(Model.GroupType)
          .has(Model.Type, Model.GroupType)
          .has(Model.Id, id.value)
          .next()
      }.toOption.map(v => v: GroupModel)

    Future { model }
  }

  def add(m: GroupModel): Vertex =
    jg
      .addV(m.`type`)
      .property(Model.Type, m.`type`)
      .property(Model.Name, m.name.value)
      .property(Model.Id, m.id.value)
      .property(Model.CreatedAt, m.createdAt.toString)
      .property(Model.ModifiedAt, m.modifiedAt.toString)
      .next()

  def remove(id: StringContainer[IdField]): Boolean = {
    Try {
      jg
        .V()
        .hasLabel(Model.GroupType)
        .has(Model.Type, Model.GroupType)
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
