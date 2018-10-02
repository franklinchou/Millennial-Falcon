package controllers

import dao.EntitlementGraph
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

    val janusConnectionStatus = EntitlementGraph.graph.isOpen

    val cassandra = {
      val host = config.get[String]("cassandra.host")
      val port = config.get[String]("cassandra.port")
      s"$host:$port"
    }

    val json =
      Json.obj(
        "application" -> "Millennial Falcon",
        "environment" -> config.get[String]("env"),
        "janus" -> { if (janusConnectionStatus) "online" else "offline" },
        "cassandra" -> cassandra
      )

    Ok(json)

  }

}
