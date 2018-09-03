package controllers

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Configuration
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.test.Helpers._
import play.api.test._


class HomeControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerTest with Injecting {

  "HomeController GET /health" should {

    "give the application name" in {
      val request = FakeRequest(GET, "/health")
      val config = mock[Configuration]
      val controller = new HomeController(stubControllerComponents(), config)
      val health = controller.health().apply(request)
      status(health) mustBe OK
      contentType(health) mustBe Some("application/json")


      val content: JsValue = contentAsJson(health)
      val applicationName = (content \ "application").validate[String].getOrElse("")
      applicationName mustBe "Millennial Falcon"
    }
  }
}
