package controllers

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers.stubControllerComponents
import play.api.test.Injecting
import services.GroupService


class GroupControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerTest with Injecting {

  import scala.concurrent.ExecutionContext.Implicits.global

  "Group Controller" should {

    val mockGroupService = mock[GroupService]

    val controller = new GroupController(stubControllerComponents(), mockGroupService)

    "create a new group" in {

    }

    "associate a user with a group" in {

    }

    "show all groups" in {

    }

    "find a group by its id" in {

    }

    "show all the users associated with a group" in {

    }


    "remove the group" in {

    }

  }

}
