package controllers

import javax.inject._
import lib.StringContainer
import lib.jsonapi.{DocumentMany, DocumentSingle, Resource}
import models.field.{GroupField, IdField}
import models.vertex.{GroupModel, GroupType, UserModel}
import play.api.libs.json._
import play.api.mvc._
import resources.{GroupResource, UserResource}
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
    Future {
      val resources: Seq[GroupResource] =
        groupService
          .findAllGroups
          .map(v => v: GroupModel)
          .map(gm => GroupResource(gm))

      val document = DocumentMany(resources, Seq.empty[JsObject], Json.obj())
      val json = Json.toJson(document)
      Ok(json)
    }
  }

  def find(id: String) = Action.async { implicit rq: Request[AnyContent] =>
    groupService
      .find(StringContainer.apply[IdField](id))
      .map { m =>
        Future {
          val resource = GroupResource(m)
          val document = DocumentSingle(resource, Seq.empty[Resource])
          val json = Json.toJson(document)
          Ok(json)
        }
      }
      .getOrElse(Future { Ok(JsNull) })
  }


  /**
    * Find all the users associated with a particular group id
    *
    * @param groupId
    * @return
    */
  def showUsers(groupId: String) = Action.async { implicit rq: Request[AnyContent] =>
    Future {
      val groupIdContainer = StringContainer.apply[IdField](groupId)

      val userResources =
        groupService
          .findAllUsers(groupIdContainer)
          .map(v => v: UserModel)
          .map(um => UserResource(um))

      val document = DocumentMany(userResources, Seq.empty[JsObject], Json.obj())
      val json = Json.toJson(document)
      Ok(json)
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
          val resource = GroupResource(model)
          val document = DocumentSingle(resource, Seq.empty[Resource])
          val json = Json.toJson(document)
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
    * @param groupId
    * @return
    */
  def associateNewUser(groupId: String) = Action(parse.tolerantJson).async {
    implicit request: Request[JsValue] => {
      val body = request.body
      val groupContainer = StringContainer[IdField](groupId) // wrapped group id
      body.validate[UserResource].fold[Future[Result]](
        _ => Future { BadRequest },
        data => {
          val user = data.userModel
          groupService.find(groupContainer).fold[Future[Result]](Future(NotFound))(_ => {
            groupService.associateNewUser(groupContainer, user.name)
            val resource = UserResource(user)
            val document = DocumentSingle(resource, Seq.empty[Resource])
            val json = Json.toJson(document)
            Future(Created(json))
          })
        })
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
      Future { NotFound }
    }
  }


}
