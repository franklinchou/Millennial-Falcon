package controllers

import dao.repos.UserRepo
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import services.UserService

@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               userService: UserService) extends AbstractController(cc) {

  def index() = Action.async { implicit rq: Request[AnyContent] =>
    userService.findAllUsers.map { models =>
      val json = Json.toJson(models)
      Ok(json)
    }
  }

}
