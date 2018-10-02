package services

import com.google.inject.ImplementedBy
import lib.StringContainer
import models.field.IdField
import models.vertex.{GroupModel, UserModel}
import org.apache.tinkerpop.gremlin.structure.Vertex

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[UserServiceJanus])
abstract class UserService()(implicit ec: ExecutionContext) {

  def findAllUsers: Future[List[UserModel]]

  /**
    * Find all groups that this user belongs to
    *
    * @param userId
    * @return
    */
  def findAllGroups(userId: StringContainer[IdField]): Future[List[GroupModel]]

  def find(id: StringContainer[IdField]): Future[Option[UserModel]]

  def add(m: UserModel): Vertex

  def remove(id: StringContainer[IdField]): Boolean

}
