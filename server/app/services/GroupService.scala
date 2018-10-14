package services

import com.google.inject.ImplementedBy
import lib.StringContainer
import models.field.{IdField, UserField}
import models.vertex.GroupModel
import org.apache.tinkerpop.gremlin.structure.Vertex

import scala.concurrent.ExecutionContext

@ImplementedBy(classOf[GroupServiceJanus])
abstract class GroupService()(implicit ec: ExecutionContext) {

  /**
    * Given the group id, find the associated vertex
    *
    * @param id Group id
    * @return
    */
  def findVertex(id: StringContainer[IdField]): Option[Vertex]

  /**
    * Find all groups/clients
    *
    * @return
    */
  def findAllGroups: Seq[Vertex]

  /**
    * Find all the users associated with a given group
    *
    * @param groupId
    * @return
    */
  def findAllUsers(groupId: StringContainer[IdField]): Seq[Vertex]

  /**
    * Add a group to the graph
    *
    * @param m
    * @return
    */
  def add(m: GroupModel): Vertex
  
  /**
    * Create a new user and associate it with a given group
    * The group must already exist. If the group does not already exist, return None.
    *
    * @param group Group id
    * @param user  Name of new user to create
    * @return
    */
  def associateNewUser(group: StringContainer[IdField], user: StringContainer[UserField]): Option[Vertex]

  /**
    * Associate an EXISTING user with an EXISTING group
    *
    * @param group Group id
    * @param user  User vertex
    * @return
    */
  def associateExistingUser(group: Vertex, user: Vertex): Unit

  /**
    * Remove a group from the graph
    *
    * @param id
    * @return
    */
  def remove(id: StringContainer[IdField]): Boolean

}