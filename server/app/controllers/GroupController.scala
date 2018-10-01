package controllers

import javax.inject._
import lib.StringContainer
import models.GroupModel
import models.fields.{GroupField, IdField}
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
      .findById(StringContainer.apply[IdField](id))
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

}
