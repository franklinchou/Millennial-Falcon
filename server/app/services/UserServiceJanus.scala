package services

import java.util

import com.google.inject.Inject
import models.{Model, UserModel}
import dao.JanusClient.jg
import org.apache.tinkerpop.gremlin.structure.Vertex

import scala.concurrent.{ExecutionContext, Future}


class UserServiceJanus @Inject()()
                                (implicit ec: ExecutionContext) extends UserService {

  def findAllUsers: Future[List[UserModel]] = {

    val allUsers: util.List[Vertex] =
      jg
        .V()
        .hasLabel(Model.UserType)
        .toList

    Future { List.empty[UserModel] }

  }

}
