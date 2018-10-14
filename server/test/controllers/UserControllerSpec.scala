package controllers

import akka.util.Timeout
import lib.StringContainer
import lib.jsonapi.DocumentMany
import models.vertex.UserModel
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.{GET, OK, contentAsJson, status, stubControllerComponents}
import play.api.test.{FakeRequest, Injecting}
import resources.UserResource
import services.{GroupService, UserServiceJanus}

import scala.concurrent.duration._

class UserControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerTest with Injecting {

  import scala.concurrent.ExecutionContext.Implicits.global

  "User Controller, show all users" should {

    val application = new GuiceApplicationBuilder()
    val testUserService = application.injector.instanceOf[UserServiceJanus]

    val mockGroupService = mock[GroupService]
    val controller = new UserController(stubControllerComponents(), testUserService, mockGroupService)

    val models = {
      List(
        UserModel.apply(StringContainer.apply("mock-1")),
        UserModel.apply(StringContainer.apply("mock-2"))
      )
    }

    models.foreach(m => testUserService.add(m)) // add models
    val request = FakeRequest(GET, s"/users")
    val method = controller.index()(request)

    "return 200" in {
      status(method)(Timeout(5.seconds)) mustBe OK
    }
    "return expected content" in {
      val content = contentAsJson(method)(Timeout(5.seconds))
      val resources = models.map(um => UserResource(um))
      val documents = DocumentMany(resources, Seq.empty[JsObject], JsObject.empty)
      val expected = Json.toJson(documents)
      assert(content.equals(expected))
    }

  }

}
