package controllers

import akka.util.Timeout
import lib.StringContainer
import models.field.GroupField
import models.vertex.GroupModel
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.{stubControllerComponents, _}
import play.api.test.{FakeHeaders, FakeRequest, Injecting}
import services.GroupServiceJanus

import scala.concurrent.duration._


class GroupControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerTest with Injecting {

  import scala.concurrent.ExecutionContext.Implicits.global

  "Group Controller" should {

    val application = new GuiceApplicationBuilder()
    val testGroupService = application.injector.instanceOf[GroupServiceJanus]
    val controller = new GroupController(stubControllerComponents(), testGroupService)

    val testGroup1 =
      """
        |{
        |	"data": {
        |		"type":"group",
        |		"id":"",
        |		"attributes":{
        |			"group":"test-group-1"
        |		}
        |	}
        |}
      """.stripMargin

    s"create a new group, should return $CREATED" in {
      val testGroup1AsJson = Json.parse(testGroup1) // Temp


      val testGroup1Model: GroupModel = {
        val g = (testGroup1AsJson \ "data" \ "attributes" \ "group").validate[String].get
        GroupModel(StringContainer.apply[GroupField](g))
      }


      // assert(testGroupService.add(testGroup1Model).isDefined)





      val request = FakeRequest(POST, s"/group", FakeHeaders(), testGroup1AsJson)
      val method = controller.create()(request)
      status(method)(Timeout(20.seconds)) mustBe CREATED
    }

    "create a new group, should return expected content" in {

    }

    "associate a new user with a group" in {

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
