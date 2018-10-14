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
import resources.UserResource
import services.{GroupService, UserService}

import scala.concurrent.duration._

class UserControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerTest with Injecting {

  import scala.concurrent.ExecutionContext.Implicits.global

  "User Controller" should {

    val mockUserService = mock[UserService]

    val mockGroupService = mock[GroupService]

    val controller = new UserController(stubControllerComponents(), mockUserService, mockGroupService)

    val models =
      List(
        UserModel.apply(StringContainer.apply("mock-1")),
        UserModel.apply(StringContainer.apply("mock-2"))
      )

    val userResources: Seq[UserResource] = models.map(um => UserResource(um))

    "show all users" ignore {
      // TODO Mock vertex?
      // when(mockUserService.findAllUsers).thenReturn(models)
      val request = FakeRequest(GET, s"/users")
      val method = controller.index()(request)

      status(method)(Timeout(5.seconds)) mustBe OK

      val content = contentAsJson(method)(Timeout(5.seconds))
      val documents = DocumentMany(userResources, Seq.empty[JsObject], JsObject.empty)
      val expected = Json.toJson(documents)
      assert(content == expected)
    }

  }

}
