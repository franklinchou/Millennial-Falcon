package services

import com.google.inject.Inject
import dao.repos.UserRepo
import models.UserModel

import scala.concurrent.{ExecutionContext, Future}


class UserServiceCassandra @Inject()(userRepo: UserRepo[UserModel])
                                    (implicit ec: ExecutionContext) {

  def findAllUsers: Future[List[UserModel]] = {
    Future { List.empty[UserModel] }
  }

}
