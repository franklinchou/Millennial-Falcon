package controllers

import javax.inject.{Inject, Singleton}
import lib.StringContainer
import lib.jsonapi.{DocumentMany, DocumentSingle}
import models.field.IdField
import play.api.libs.json._
import play.api.mvc._
import resources.{FeatureResource, GroupResource}
import services.FeatureService

import scala.concurrent.ExecutionContext

@Singleton
class FeatureController @Inject()(cc: ControllerComponents,
                                  featureService: FeatureService)
                                 (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def index() = Action.async { implicit request: Request[AnyContent] =>
    featureService
      .findAllFeatures
      .map {
        models =>
          if (models.isEmpty) {
            Ok(JsArray.empty)
          } else {
            val resources = models.map(m => FeatureResource(m))
            val document = DocumentMany(resources, Seq.empty[JsObject], Json.obj())
            val json = Json.toJson(document)
            Ok(json)
          }
      }
  }


  def find(id: String) = Action.async { implicit rq: Request[AnyContent] =>
    val idContainer = StringContainer.apply[IdField](id)
    for {
      featureOpt <- featureService.find(idContainer)
    } yield {
      featureOpt
        .map { f =>
          val resource = FeatureResource(f)
          val document = DocumentSingle(resource, Seq.empty[JsObject])
          val json = Json.toJson(document)
          Ok(json)
        }
        .getOrElse(Ok(JsNull))
    }
  }


  def create() = Action(parse.tolerantJson) {
    implicit request: Request[JsValue] =>
      val body = request.body
      val result = body.validate[FeatureResource]

      result.fold(
        _ => {
          BadRequest // TODO Fill with error
        },
        data => {
          val model = data.featureModel
          val _ = featureService.add(model)
          val resource = FeatureResource(model)
          val document = DocumentSingle(resource, Seq.empty[JsObject])
          val json = Json.toJson(document)
          Created(json)
        }
      )
  }

}
