package services

import com.google.inject.Inject
import models.UserModel

import scala.concurrent.{ExecutionContext, Future}


class UserServiceJanus @Inject()()
                                (implicit ec: ExecutionContext) extends UserService {

  def findAllUsers: Future[List[UserModel]] = {
    Future { List.empty[UserModel] }
  }

}
