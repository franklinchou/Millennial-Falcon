package models

import java.util.NoSuchElementException

import dao.JanusClient.jg
import lib.StringContainer
import models.fields.UserField
import org.scalatest.FunSpec
import org.scalatest.mockito.MockitoSugar
import play.api.inject.guice.GuiceApplicationBuilder
import services.UserServiceJanus

class UserModelSpec extends FunSpec with MockitoSugar {

  // https://www.playframework.com/documentation/2.6.x/ScalaTestingWithGuice
  val application = new GuiceApplicationBuilder()

  val userService = application.injector.instanceOf[UserServiceJanus]

  val mockUser1 = UserModel.apply(StringContainer[UserField]("user1"))
  val mockUser2 = UserModel.apply(StringContainer[UserField]("user2"))
  val mockUsers = Seq(mockUser1, mockUser2)

  // TODO Should this be UserModel?
  private def setUp(): Unit = mockUsers.foreach(mu => userService.add(mu))

  describe("A User Model") {
    it("should insert into Janus Graph") {

      /**
        * In order to use indexing, query must contain vertex label.
        * This query will use the "userNameComposite" index ("user-name-index").
        */
      lazy val expected =
        jg
          .V()
          .hasLabel(Model.UserType)
          .has(Model.Name, "user1")
          .next()
          .property(Model.Name)
          .value()
          .toString

      assertThrows[NoSuchElementException] {
        expected
      }

      setUp()

      assert(expected == "user1")
    }
  }

}
