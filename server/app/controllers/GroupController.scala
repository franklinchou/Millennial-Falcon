package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import services.GroupService

import scala.concurrent.ExecutionContext

@Singleton
class GroupController @Inject()(cc: ControllerComponents,
                                groupService: GroupService)
                               (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def index() = Action.async { implicit rq: Request[AnyContent] =>
    groupService
      .findAllGroups
      .map { models =>
        val json = Json.toJson(models)
        Ok(json)
      }
  }

}
