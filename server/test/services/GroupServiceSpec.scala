package services

import dao.JanusClient.jg
import lib.StringContainer
import models.field.{GroupField, UserField}
import models.vertex.GroupModel
import org.scalatest.AsyncFunSpec
import play.api.inject.guice.GuiceApplicationBuilder

class GroupServiceSpec extends AsyncFunSpec {

  val application = new GuiceApplicationBuilder()

  val userService = application.injector.instanceOf[UserServiceJanus]

  val groupService = application.injector.instanceOf[GroupServiceJanus]

  describe("Group Service") {

    it("should allow group -> user association") {
      val mockGroup = GroupModel.apply(StringContainer[GroupField]("mock-group"))
      val mockUserName = StringContainer[UserField]("mock-user")

      val groupId = mockGroup.id
      val addedGroup = groupService.add(mockGroup)
      val result = groupService.associateNewUser(groupId, mockUserName)  // return the resulting user as vertex

      val query = jg.V(addedGroup.id()).out().toList

      assert(query.contains(result.get))
    }

  }

}
