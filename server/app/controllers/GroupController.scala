package controllers

import javax.inject._
import lib.StringContainer
import models.field.{GroupField, IdField, UserField}
import models.vertex.GroupModel
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.GroupService

import scala.concurrent.{ExecutionContext, Future}

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

  def findById(id: String) = Action.async { implicit rq: Request[AnyContent] =>
    groupService
      .find(StringContainer.apply[IdField](id))
      .map { m =>
        val json = Json.toJson(m)
        Ok(json)
      }
  }


  /**
    * Find all the users associated with a particular group id
    *
    * @param groupId
    * @return
    */
  def showUsers(groupId: String) = Action.async { implicit rq: Request[AnyContent] =>

    val groupIdContainer = StringContainer.apply[IdField](groupId)

    groupService
      .findAllUsers(groupIdContainer)
      .map { m =>
        val json = Json.toJson(m)
        Ok(json)
      }
  }


  /**
    * Create a new group
    *
    * POST => { "group" : "test-group" }
    *
    * @return
    */
  def create() = Action(parse.tolerantJson).async {
    implicit rq: Request[JsValue] => {

      val body = rq.body

      val groupAsOpt = (body \ "group").validate[String].asOpt

      val validate = Seq(groupAsOpt).forall(_.isDefined)

      if (validate) {
        val group = StringContainer.apply[GroupField](groupAsOpt.get)
        val model = GroupModel.apply(group)
        Future {
          val _ = groupService.add(model)
          val json = Json.toJson[GroupModel](model)
          Created(json)
        }
      } else {
        Future { BadRequest }
      }
    }
  }

  /**
    * Create a new user and associate it with the given group.
    *
    * The group MUST exist prior to adding a user.
    *
    * POST => { "user" : "user-id" }
    *
    * @param groupId
    * @return
    */
  def associateUser(groupId: String) = Action(parse.tolerantJson).async {
    implicit request: Request[JsValue] => {
      val body = request.body
      val userId = (body \ "user").validate[String].get

      val gid = StringContainer[IdField](groupId)  // wrapped group id
      val uid = StringContainer[UserField](userId)
      groupService.find(gid).map { g =>
        if (g.isDefined) {

          // Create new user
          val _ = groupService.associateUser(gid, uid)

          val model = g.get
          Ok(Json.toJson(model))
        } else {
          NotFound
        }
      }
    }
  }

}
