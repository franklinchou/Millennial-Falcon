package controllers

import javax.inject._
import lib.StringContainer
import models.field.{GroupField, IdField}
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


  /**
    * Given a user id determine which group that user belongs to, or null
    *
    * @param id
    * @return
    */
  def whichGroup(id: String) = Action.async { implicit rq: Request[AnyContent] =>
    val groupId = StringContainer.apply[IdField](id)
    userService
      .findGroup(groupId)
      .map { models =>
        val json = Json.toJson(models)
        Ok(json)
      }
  }


  /**
    * Delete a user based on its id
    *
    * @param id
    * @return
    */
  def delete(id: String) = Action.async { implicit request: Request[AnyContent] =>
    val userId = StringContainer.apply[IdField](id)
    if (userService.remove(userId)) {
      Future { NoContent }
    } else {
      Future { InternalServerError }
    }
  }

}
