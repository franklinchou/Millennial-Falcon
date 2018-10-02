package services

import com.google.inject.ImplementedBy
import lib.StringContainer
import models.field.{IdField, UserField}
import models.vertex.{GroupModel, UserModel}
import org.apache.tinkerpop.gremlin.structure.Vertex

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[GroupServiceJanus])
abstract class GroupService()(implicit ec: ExecutionContext) {

  /**
    * Find a single group by its id
    *
    * @param id
    * @return
    */
  def find(id: StringContainer[IdField]): Future[Option[GroupModel]]

  /**
    * Find all groups/clients
    *
    * @return
    */
  def findAllGroups: Future[List[GroupModel]]

  /**
    * Find all the users associated with a given group
    *
    * @param groupId
    * @return
    */
  def findAllUsers(groupId: StringContainer[IdField]): Future[List[UserModel]]

  def add(m: GroupModel): Vertex

  def remove(id: StringContainer[IdField]): Boolean

  /**
    * Create a new user and associate it with a given group
    *
    * The group must already exist. If the group does not already exist, return None.
    *
    * @param group Group id
    * @param user  Name of new user to create
    * @return
    */
  def associateUser(group: StringContainer[IdField], user: StringContainer[UserField]): Option[Vertex]

}