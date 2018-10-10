package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.JsArray
import play.api.mvc._
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
          Ok(JsArray.empty)
      }
  }


}
