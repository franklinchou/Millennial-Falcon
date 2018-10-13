package controllers

import javax.inject._
import lib.StringContainer
import lib.jsonapi.{DocumentMany, DocumentSingle, Resource}
import models.field.IdField
import play.api.libs.json._
import play.api.mvc._
import resources.{FeatureIdResource, GroupResource, UserResource}
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
    val userId = StringContainer.apply[IdField](id)
    userService
      .findGroup(userId)
      .map { userModelOpt =>
        userModelOpt
          .map { m =>
            val resource = GroupResource(m)
            val document = DocumentSingle(resource, Seq.empty[Resource])
            val json = Json.toJson(document)
            Ok(json)
          }
          .getOrElse(Ok(JsNull))
      }

  }


  /**
    * Determine which features a given user has access to
    *
    * @param id
    * @return
    */
  def whichFeatures(id: String) = Action.async { implicit rq: Request[AnyContent] =>
    val userId = StringContainer.apply[IdField](id)
    userService
      .findFeatures(userId)
      .map { featureModels =>
        val resource = featureModels.map(fm => FeatureIdResource(fm.id.value))
        val document = DocumentMany(resource, Seq.empty[JsObject], JsObject.empty)
        val json: JsValue = Json.toJson(document)
        Ok(json)
      }
  }


  /**
    * Given a user id associate features to that user
    *
    * @param id
    */
  def associateFeatures(id: String) = Action(parse.tolerantJson).async {
    implicit rq: Request[JsValue] => {

      val body = rq.body
      val userContainer = StringContainer[IdField](id)

      body.validate[List[FeatureIdResource]].fold[Future[Result]](
        _ => Future { BadRequest },
        valid => {
          userService.find(userContainer).map { userOpt =>
            userOpt.fold[Result](NotFound)(user => {
              valid
                .map(featureId => StringContainer.apply[IdField](featureId.id))
                .map(featureContainer => userService.associateFeature(userContainer, featureContainer))
              val resource = UserResource(user)
              val associated = valid
              val document = DocumentSingle(resource, associated)
              val json = Json.toJson(document)
              Created(json)
            })
          }
        }
      )
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
