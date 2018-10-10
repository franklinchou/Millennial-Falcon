package controllers

import javax.inject.{Inject, Singleton}
import lib.jsonapi.{DocumentMany, DocumentSingle}
import models.vertex.FeatureModel
import models.vertex.FeatureModel._
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import play.api.mvc._
import resources.FeatureResource
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
            // val resources = models.map(m => FeatureResource.apply(m))
            // TODO Cut over to use Resource
            val resources = models.map(m => Json.toJsObject[FeatureModel](m))
            val document = DocumentMany(resources, Seq.empty[JsObject], Json.obj())
            val json = Json.toJson(document)
            Ok(json)
          }
      }
  }


  def create() = Action(parse.tolerantJson) {
    implicit request: Request[JsValue] =>
      val body = request.body
      val result = body.validate[FeatureResource]

      result.fold(
        _ => {
          BadRequest  // TODO Fill with error
        },
        data => {
          val model = data.model
          featureService.add(model)
          val json = Json.toJsObject(model)
          val document = DocumentSingle(json, Seq.empty[JsObject])
          Created(Json.toJson(document))  // TODO Use Resource here
        }
      )

  }


}
