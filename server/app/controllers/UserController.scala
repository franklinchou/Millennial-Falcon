package controllers

import javax.inject._
import lib.StringContainer
import lib.jsonapi.{DocumentMany, DocumentSingle, Resource}
import models.field.{GroupField, IdField}
import models.vertex.{FeatureModel, GroupModel, UserModel}
import play.api.libs.json._
import play.api.mvc._
import resources.{FeatureIdResource, FeatureResource, GroupResource, UserResource}
import services.{GroupService, UserService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               userService: UserService,
                               groupService: GroupService)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def index() = Action.async { implicit rq: Request[AnyContent] =>
    Future {
      val resources: Seq[UserResource] =
        userService
          .findAllUsers
          .map(u => u: UserModel)
          .map(um => UserResource(um))

      val document = DocumentMany(resources, Seq.empty[JsObject], Json.obj())
      val json = Json.toJson(document)
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
    val userId = StringContainer.apply[IdField](id)
    userService
      .findGroupVertexByUser(userId)
      .map(v => v:GroupModel)
      .map { group =>
        Future {
          val resource = GroupResource(group)
          val document = DocumentSingle(resource, Seq.empty[Resource])
          val json = Json.toJson(document)
          Ok(json)
        }
      }
      .getOrElse(Future { Ok(JsNull) })
  }


  /**
    * Determine which features a given user has access to
    *
    * @param id
    * @return
    */
  def showFeatures(id: String) = Action.async { implicit rq: Request[AnyContent] =>
    Future {
      val userId = StringContainer.apply[IdField](id)
      val resources: Seq[FeatureResource] =
        userService
          .findFeatures(userId)
          .map(v => v: FeatureModel)
          .map(fm => FeatureResource(fm))

      val document = DocumentMany(resources, Seq.empty[JsObject], Json.obj())
      val json = Json.toJson(document)
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
          // Insert the feature relationship
          valid.foreach { feature =>
            val id = StringContainer.apply[IdField](feature.id)
            userService.associateFeature(userContainer, id)
          }
          userService
            .findUserVertex(userContainer)
            .map(v => v: UserModel)
            .map { user =>
              Future {
                val resource = UserResource(user)
                val associated = valid
                val document = DocumentSingle(resource, associated)
                val json = Json.toJson(document)
                Created(json)
              }
            }
            .getOrElse(Future { NotFound })
        }
      )
    }
  }

  /**
    * Update the given user to a new group
    *
    * If the group does not exist, create a group and return 201
    * If the update was successful return 200
    * The user MUST exist (or return 404)
    *
    * @param id
    */
  def updateGroup(id: String) = Action(parse.tolerantJson).async {
    implicit rq: Request[JsValue] => {

      val body = rq.body
      val data = body \ "data"

      // val typeAsOpt = (data \ "type").validate[String].asOpt.filter(_.equals(GroupType))
      val groupIdAsOpt = (data \ "id").validate[String].asOpt

      val userContainer = StringContainer.apply[IdField](id)
      val groupContainer = StringContainer.apply[IdField](groupIdAsOpt.get)

      val userOpt = userService.findUserVertex(userContainer)
      val groupOpt = groupService.findVertex(groupContainer)

      // TODO Clean this up after #35
      (userOpt, groupOpt) match {
        case (Some(user), Some(group)) =>
          userService.removeGroup(userContainer)
          groupService.associateExistingUser(group, user)
          val resource = UserResource(user)
          val groupResource = GroupResource(group)
          val document = DocumentSingle(resource, Seq(groupResource))
          val json = Json.toJson(document)
          Future { Ok(json) }
        case (Some(user), None) =>
          val groupNameAsOpt = (data \ "attributes" \ "group").validate[String].asOpt
          val groupName = StringContainer.apply[GroupField](groupNameAsOpt.get)
          val groupVertex = groupService.add(GroupModel.apply(groupName))
          val _ = groupService.associateExistingUser(user, groupVertex)
          val resource = UserResource(user)
          val groupResource = GroupResource(groupVertex)
          val document = DocumentSingle(resource, Seq(groupResource))
          val json = Json.toJson(document)
          Future { Created(json) }
        case (None, _) =>
          Future { NotFound }
      }
    }
  }


  /**
    * Remove the relationship between user <> feature
    *
    * @param user
    * @param feature
    * @return
    */
  def removeFeature(user: String, feature: String) = Action.async {
    implicit request: Request[AnyContent] => {
      val userContainer = StringContainer.apply[IdField](user)
      val featureContainer = StringContainer.apply[IdField](feature)
      if (userService.removeFeature(userContainer, featureContainer)) {
        Future { NoContent }
      } else {
        Future { NotFound }
      }
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
      Future { NotFound }
    }
  }

}
