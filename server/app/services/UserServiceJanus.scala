package services

import com.google.inject.Inject
import dao.JanusClient.jg
import models.{Model, UserModel}
import org.apache.tinkerpop.gremlin.structure.Vertex
import utils.ListConversions._

import scala.concurrent.{ExecutionContext, Future}


class UserServiceJanus @Inject()()
                                (implicit ec: ExecutionContext) extends UserService {

  def findAllUsers: Future[List[UserModel]] = {

    val allUsers: List[Vertex] =
      jg
        .V()
        .hasLabel(Model.UserType)
        .toList

    

    Future { List.empty[UserModel] }

  }

}
