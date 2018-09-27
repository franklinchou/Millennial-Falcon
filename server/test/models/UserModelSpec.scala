package models

import java.util.NoSuchElementException

import dao.JanusClient.jg
import lib.StringContainer
import models.fields.ModelUserName
import org.scalatest.FunSpec
import play.api.inject.guice.GuiceApplicationBuilder

class UserModelSpec extends FunSpec {

  // https://www.playframework.com/documentation/2.6.x/ScalaTestingWithGuice
  val application = new GuiceApplicationBuilder()

  val mockUser1 = UserModel(userName = StringContainer[ModelUserName]("user1"))
  val mockUser2 = UserModel(userName = StringContainer[ModelUserName]("user2"))
  val mockUsers = Seq(mockUser1, mockUser2)

  private def setUp(): Unit = mockUsers.foreach(mu => UserModel.add(mu, jg))

  describe("A User Model") {
    it("should insert into Janus Graph") {

      /**
        * In order to use indexing, query must contain vertex label.
        * This query will use the "userNameComposite" index ("user-name-index").
        */
      lazy val expected =
        jg
          .V()
          //.hasLabel(Model.UserType)
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
