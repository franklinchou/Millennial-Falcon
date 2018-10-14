package services

import com.google.inject.ImplementedBy
import lib.StringContainer
import models.field.{FeatureField, IdField}
import models.vertex.{FeatureModel, GroupModel, UserModel}
import org.apache.tinkerpop.gremlin.structure.Vertex

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[UserServiceJanus])
abstract class UserService()(implicit ec: ExecutionContext) {

  def findAllUsers: Future[List[UserModel]]

  /**
    * Find which group a user belongs to
    *
    * @return
    */
  def findGroup(id: StringContainer[IdField]): Option[GroupModel]

  /**
    * Find which features this user has access to
    *
    * @param id user id
    * @return
    */
  def findFeatures(id: StringContainer[IdField]): Future[List[FeatureModel]]

  def find(id: StringContainer[IdField]): Option[UserModel]

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

  def remove(id: StringContainer[IdField]): Boolean

}
