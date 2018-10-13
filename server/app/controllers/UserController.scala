package controllers

import javax.inject._
import lib.StringContainer
import lib.jsonapi.{DocumentMany, DocumentSingle}
import models.field.IdField
import play.api.libs.json.{JsArray, JsNull, JsObject, Json}
import play.api.mvc._
import resources.{GroupResource, UserResource}
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
        if (models.isEmpty) {
          Ok(JsArray.empty)
        } else {
          val resources = models.map(um => UserResource(um))
          val document = DocumentMany(resources, Seq.empty[JsObject], Json.obj())
          val json = Json.toJson(document)
          Ok(json)
        }
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
      .map { userModelOpt =>
        userModelOpt
          .map { m =>
            val json = GroupResource(m)
            val document = DocumentSingle(json, Seq.empty[JsObject])
            Ok(Json.toJson(document))
          }
          .getOrElse(Ok(JsNull))
      }

  }


  /**
    * Given a user id associate features to that user
    *
    * @param id
    */
  def associateFeatures(id: String) = Action.async { implicit rq: Request[AnyContent] =>

    Future { Ok }
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
