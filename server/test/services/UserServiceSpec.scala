package services

import java.util.NoSuchElementException

import dao.JanusClient.jg
import lib.StringContainer
import models.field.UserField
import models.vertex
import models.vertex.UserModel
import org.scalatest.AsyncFunSpec
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

class UserServiceSpec extends AsyncFunSpec {

  // https://www.playframework.com/documentation/2.6.x/ScalaTestingWithGuice
  val application = new GuiceApplicationBuilder()

  val userService = application.injector.instanceOf[UserServiceJanus]

  val mockUser1 = UserModel.apply(StringContainer[UserField]("user1"))

  val mockUser2 = UserModel.apply(StringContainer[UserField]("user2"))

  val mockUsers = Seq(mockUser1, mockUser2)

  private def setUp(): Unit = mockUsers.foreach(mu => userService.add(mu))

  describe("User Service") {
    it("should insert into Janus Graph") {

      /**
        * In order to use indexing, query must contain vertex label.
        * This query will use the "userNameComposite" index ("user-name-index").
        */
      lazy val expected =
        jg
          .V()
          .hasLabel(vertex.UserType)
          .has(vertex.Name, "user1")
          .next()
          .property(vertex.Name)
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

    it("should convert from Vertex -> UserModel") {
      userService.findById(mockUser1.id).map(m => assert(m.contains(mockUser1)))
    }

    it("should support delete") {
      val result = userService.remove(mockUser1.id)
      
      // Test
      assert(result)
      userService.findAllUsers.map(u => assert(u.size == 1))

      // TODO This test is flaky
      // userService.findById(mockUser1.id).map(m => assert(m.isEmpty))
    }
  }
}
