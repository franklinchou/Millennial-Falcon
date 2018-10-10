package controllers

import akka.util.Timeout
import lib.StringContainer
import lib.jsonapi.DocumentMany
import models.vertex.UserModel
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.{GET, OK, contentAsJson, status, stubControllerComponents}
import play.api.test.{FakeRequest, Injecting}
import services.UserService

import scala.concurrent.Future
import scala.concurrent.duration._

class UserControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerTest with Injecting {

  import scala.concurrent.ExecutionContext.Implicits.global

  "User Controller" should {

    val mockUserService = mock[UserService]

    val controller = new UserController(stubControllerComponents(), mockUserService)

    val models =
      List(
        UserModel.apply(StringContainer.apply("mock-1")),
        UserModel.apply(StringContainer.apply("mock-2"))
      )

    val jsonModels: Seq[JsObject] = models.map(um => Json.toJsObject[UserModel](um))

    "show all users" in {
      when(mockUserService.findAllUsers).thenReturn(Future { models })
      val request = FakeRequest(GET, s"/users")
      val method = controller.index()(request)

      status(method)(Timeout(5.seconds)) mustBe OK

      val content = contentAsJson(method)(Timeout(5.seconds))
      val expected = Json.toJson(DocumentMany(jsonModels, Seq.empty[JsObject], JsObject.empty))

      assert(content == expected)
    }

  }

}
