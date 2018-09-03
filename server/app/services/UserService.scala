package services

import com.google.inject.ImplementedBy
import models.UserModel

import scala.concurrent.{ExecutionContext, Future}


@ImplementedBy(classOf[UserServiceCassandra])
abstract class UserService()(implicit ec: ExecutionContext) {


  def findAllUsers: Future[List[UserModel]]


}