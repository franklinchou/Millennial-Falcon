package services

import java.util.NoSuchElementException

import dao.JanusClient.jg
import lib.StringContainer
import models.fields.UserField
import models.{Model, UserModel}
import org.scalatest.AsyncFunSpec
import play.api.inject.guice.GuiceApplicationBuilder

class UserServiceSpec extends AsyncFunSpec {

  // https://www.playframework.com/documentation/2.6.x/ScalaTestingWithGuice
  val application = new GuiceApplicationBuilder()

  val userService = application.injector.instanceOf[UserServiceJanus]

  val mockUser1 = UserModel.apply(StringContainer[UserField]("user1"))
  val mockUser2 = UserModel.apply(StringContainer[UserField]("user2"))
  val mockUsers = Seq(mockUser1, mockUser2)

  private def setUp(): Unit = mockUsers.foreach(mu => userService.add(mu))

  describe("A User Service") {
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

      setUp() // Set up mock
      assert(expected == "user1")
    }

    it("should support find all") {
      userService.findAllUsers.map(u => assert(u.size == 2))
    }

    it("should convert from Vertex => UserModel") {
      userService.findById(mockUser1.id).map(m => assert(m.contains(mockUser1)))
    }

    it("should support delete") {
      userService.remove(mockUser1.id)

      // Test
      userService.findAllUsers.map(u => assert(u.size == 1))
      userService.findById(mockUser1.id).map(m => assert(m.isEmpty))
    }
  }
}
