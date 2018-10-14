package services

import com.google.inject.ImplementedBy
import lib.StringContainer
import models.field.IdField
import models.vertex.UserModel
import org.apache.tinkerpop.gremlin.structure.Vertex

import scala.concurrent.ExecutionContext

@ImplementedBy(classOf[UserServiceJanus])
abstract class UserService()(implicit ec: ExecutionContext) {

  /**
    * Find a user by its id and return the user vertex
    *
    * @param id
    * @return
    */
  def findUserVertex(id: StringContainer[IdField]): Option[Vertex]

  /**
    * Find all the users in the graph
    *
    * @return
    */
  def findAllUsers: Seq[Vertex]

  /**
    * Return the group vertex to which this user belongs
    *
    * @param id
    * @return
    */
  def findGroupVertexByUser(id: StringContainer[IdField]): Option[Vertex]

  /**
    * Find which features this user has access to
    *
    * @param id user id
    * @return
    */
  def findFeatures(id: StringContainer[IdField]): Seq[Vertex]

  /**
    * Add a user to the graph
    *
    * @param m
    * @return
    */
  def add(m: UserModel): Vertex

  /**
    * Associate an EXISTING user with an EXISTING feature
    *
    * @param user
    * @param feature
    */
  def associateFeature(user: StringContainer[IdField], feature: StringContainer[IdField]): Option[Vertex]

  /**
    * Dissociate an EXISTING user from an EXISTING feature
    *
    * @param user
    * @param feature
    * @return
    */
  def removeFeature(user: StringContainer[IdField], feature: StringContainer[IdField]): Boolean

  /**
    * Dissociate an EXISTING user from its group
    * There should only ever be one group!
    *
    * @param user
    * @return
    */
  def removeGroup(user: StringContainer[IdField]): Boolean

  /**
    * Remove a user from the graph
    *
    * @param id
    * @return
    */
  def remove(id: StringContainer[IdField]): Boolean

}
