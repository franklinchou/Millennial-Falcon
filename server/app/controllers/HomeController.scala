package controllers

import javax.inject._
import play.api._
import play.api.libs.json.Json
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               config: Configuration) extends AbstractController(cc) {

  def health() = Action { implicit request: Request[AnyContent] =>

    val json =
      Json.obj(
        "application" -> "Millennial Falcon",
        "environment" -> config.get[String]("env")
      )

    Ok(json)

  }

}
