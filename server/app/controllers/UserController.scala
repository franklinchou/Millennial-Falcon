package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import services.UserService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               userService: UserService)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def index() = Action.async { implicit rq: Request[AnyContent] =>
    userService
      .findAllUsers
      .map { models =>
        val json = Json.toJson(models)
        Ok(json)
      }
  }


  def showGroups(id: String) = Action.async {
    implicit rq: Request[AnyContent] =>

      // TODO 
      Future { Ok }
  }

}
