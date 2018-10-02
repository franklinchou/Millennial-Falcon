package controllers

import akka.util.Timeout
import lib.StringContainer
import models.vertex.UserModel
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers.{GET, OK, status, stubControllerComponents}
import play.api.test.{FakeRequest, Injecting}
import services.UserService

import scala.concurrent.Future
import scala.concurrent.duration._

class UserControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerTest with Injecting {

  import scala.concurrent.ExecutionContext.Implicits.global

  "User Controller" should {

    val mockUserService = mock[UserService]

    val controller = new UserController(stubControllerComponents(), mockUserService)

    val mockResults =
      List(
        UserModel.apply(StringContainer.apply("mock-1")),
        UserModel.apply(StringContainer.apply("mock-2"))
      )

    "show all users" in {
      when(mockUserService.findAllUsers).thenReturn(Future { mockResults })
      val request = FakeRequest(GET, s"/users")
      val method = controller.index()(request)

      // TODO check status for now, how to check return value?
      status(method)(Timeout(5.seconds)) mustBe OK
    }

  }


}
