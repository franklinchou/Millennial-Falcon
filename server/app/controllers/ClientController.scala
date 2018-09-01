package controllers

import com.google.inject.Inject
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

@Singleton
class ClientController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action { implicit rq: Request[AnyContent] =>
    Ok()
  }

}
