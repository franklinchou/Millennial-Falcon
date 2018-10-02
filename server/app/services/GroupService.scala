package services

import com.google.inject.ImplementedBy
import lib.StringContainer
import models.GroupModel
import models.fields.IdField
import org.apache.tinkerpop.gremlin.structure.Vertex

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[GroupServiceJanus])
abstract class GroupService()(implicit ec: ExecutionContext) {

  /**
    * Find all groups/clients
    *
    * @return
    */
  def findAllGroups: Future[List[GroupModel]]

  def find(id: StringContainer[IdField]): Future[Option[GroupModel]]

  def add(m: GroupModel): Vertex

  def remove(id: StringContainer[IdField]): Boolean

}