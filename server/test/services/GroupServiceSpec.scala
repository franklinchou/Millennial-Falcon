package services

import lib.StringContainer
import models.field.{GroupField, UserField}
import models.vertex.{GroupModel, UserModel}
import org.scalatest.AsyncFunSpec
import play.api.inject.guice.GuiceApplicationBuilder
import dao.JanusClient.jg

class GroupServiceSpec extends AsyncFunSpec {

  val application = new GuiceApplicationBuilder()

  val userService = application.injector.instanceOf[UserServiceJanus]

  val groupService = application.injector.instanceOf[GroupServiceJanus]

  describe("Group Service") {

    it("should allow user -> group association") {
      val mockGroup = GroupModel.apply(StringContainer[GroupField]("mock-group"))
      val mockUserName = StringContainer[UserField]("mock-user")

      val groupId = mockGroup.id
      val addedGroup = groupService.add(mockGroup)
      val result = groupService.associateUser(groupId, mockUserName).get  // return the resulting user as vertex

      val query = jg.V(result.id).out().toList

      assert(query.contains(addedGroup))

      // Negative test
      val fakeUserModel = UserModel.apply(StringContainer[UserField]("fake-user"))
      val addedUser = userService.add(fakeUserModel)

      val addedUserId = addedUser.id

      assert(jg.V(addedUserId).out().toList.isEmpty)
    }

  }

}
