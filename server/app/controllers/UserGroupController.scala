package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class UserGroupController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action { implicit rq: Request[AnyContent] =>
    Ok("")
  }

}
