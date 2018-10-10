package controllers

import javax.inject._
import lib.StringContainer
import lib.jsonapi.{DocumentMany, DocumentSingle}
import models.field.{GroupField, IdField, UserField}
import models.vertex.{GroupModel, GroupType}
import play.api.libs.json.{JsNull, JsObject, JsValue, Json}
import play.api.mvc._
import services.GroupService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GroupController @Inject()(cc: ControllerComponents,
                                groupService: GroupService)
                               (implicit ec: ExecutionContext) extends AbstractController(cc) {

  /**
    * Find all the groups
    *
    * @return
    */
  def index() = Action.async { implicit rq: Request[AnyContent] =>
    groupService
      .findAllGroups
      .map { models =>
        val resources = models.map(m => Json.toJsObject[GroupModel](m))
        val document = DocumentMany(resources, Seq.empty[JsObject], Json.obj())
        val json = Json.toJson(document)
        Ok(json)
      }
  }

  def find(id: String) = Action.async { implicit rq: Request[AnyContent] =>
    groupService
      .find(StringContainer.apply[IdField](id))
      .map { groupModelOpt =>
        groupModelOpt
          .map { m =>  // TODO Change to for-comprehension?
            val json = Json.toJsObject[GroupModel](m)
            val document = DocumentSingle(json, Seq.empty[JsObject])
            Ok(Json.toJson(document))
          }
          .getOrElse(Ok(JsNull))
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
      val data = body \ "data"

      val typeAsOpt = (data \ "type").validate[String].asOpt.filter(_.equals(GroupType))
      val groupAsOpt = (data \ "attributes" \ "group").validate[String].asOpt

      val validate = Seq(groupAsOpt, typeAsOpt).forall(_.isDefined)

      if (validate) {
        val group = StringContainer.apply[GroupField](groupAsOpt.get)
        val model = GroupModel.apply(group)
        Future {
          val _ = groupService.add(model)
          val json = Json.toJsObject[GroupModel](model)
          val document = DocumentSingle(json, Seq.empty[JsObject])
          Created(Json.toJson(document))
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

  /**
    * Delete a group based on its id
    *
    * @param id
    * @return
    */
  def delete(id: String) = Action.async { implicit request: Request[AnyContent] =>
    val groupId = StringContainer.apply[IdField](id)
    if (groupService.remove(groupId)) {
      Future { NoContent }
    } else {
      Future { InternalServerError }
    }
  }


}
