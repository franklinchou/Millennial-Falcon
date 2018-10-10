package controllers

import javax.inject._
import lib.StringContainer
import lib.jsonapi.{DocumentMany, DocumentSingle}
import models.field.{GroupField, IdField, UserField}
import models.vertex.{GroupModel, GroupType, UserModel, UserType}
import play.api.libs.json._
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
        if (models.isEmpty) {
          Ok(JsArray.empty)  // TODO Get rid of this ugly logic by wrapping in a Monad?
        } else {
          val resources = models.map(m => Json.toJsObject[GroupModel](m))
          val document = DocumentMany(resources, Seq.empty[JsObject], Json.obj())
          val json = Json.toJson(document)
          Ok(json)
        }
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
      .map { models =>
        if (models.isEmpty) {
          Ok(JsArray.empty)
        } else {
          val resources = models.map(m => Json.toJsObject[UserModel](m))
          val document = DocumentMany(resources, Seq.empty[JsObject], Json.obj())
          val json = Json.toJson(document)
          Ok(json)
        }
      }
  }


  /**
    * Create a new group
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
    * @param groupId
    * @return
    */
  def associateUser(groupId: String) = Action(parse.tolerantJson).async {
    implicit request: Request[JsValue] => {
      val body = request.body
      val data = body \ "data"

      val typeOpt = (data \ "type").validate[String].asOpt.filter(_.equals(UserType))
      val userIdOpt = (data \ "attributes" \ "user").validate[String].asOpt

      val validate = Seq(userIdOpt, typeOpt).forall(_.isDefined)

      if (validate) {
        val gid = StringContainer[IdField](groupId)  // wrapped group id
        val uid = StringContainer[UserField](userIdOpt.get)
        groupService.find(gid).map { g =>
          if (g.isDefined) {
            val _ = groupService.associateUser(gid, uid)  // Create new user
            val json = Json.toJsObject[GroupModel](g.get)
            val document = DocumentSingle(json, Seq.empty[JsObject])
            Created(Json.toJson(document))
          } else {
            NotFound
          }
        }
      } else {
        Future { BadRequest }
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
