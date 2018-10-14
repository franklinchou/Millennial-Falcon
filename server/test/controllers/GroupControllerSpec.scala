package controllers

import akka.util.Timeout
import models.vertex.GroupModel
import models.vertex.GroupModel._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Logger
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsArray, Json}
import play.api.test.Helpers.{stubControllerComponents, _}
import play.api.test.{FakeHeaders, FakeRequest, Injecting}
import services.GroupServiceJanus

import scala.concurrent.duration._


class GroupControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerTest with Injecting {

  import scala.concurrent.ExecutionContext.Implicits.global

  val application = new GuiceApplicationBuilder()
  val testGroupService = application.injector.instanceOf[GroupServiceJanus]
  val controller = new GroupController(stubControllerComponents(), testGroupService)

  "Group Controller, create a new group" should {

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

    val testGroup1AsJson = Json.parse(testGroup1)
    val request = FakeRequest(POST, s"/group", FakeHeaders(), testGroup1AsJson)
    val method = controller.create()(request)
    val content = contentAsJson(method)(Timeout(20.seconds))

    s"return $CREATED" in {
      status(method)(Timeout(20.seconds)) mustBe CREATED
    }

    "return expected content" in {
      (content \ "data" \ "attributes" \ "group")
        .validate[String]
        .fold(
          _ => assert(false),
          g => assert(g == "test-group-1")
        )
    }

    "return the created group's id" in {
      (content \ "data" \ "id")
        .validate[String]
        .fold(
          _ => assert(false),
          id => {
            // createdGroup = id
            Logger.info(s"Successfully created group with id=$id")
            assert(true)
          }
        )
    }
  }


  "Group Controller, associate a new user" should {

    val testGroup2Id = "123e4567-e89b-12d3-a456-426655440000"
    val testGroup2Model = GroupModel.apply(testGroup2Id, "test-group-2")

    val testUser1 =
      """
        |{
        |	"data": {
        |		"type":"user",
        |		"id":"",
        |		"attributes":{
        |			"user":"test-user-1"
        |		}
        |	}
        |}
      """.stripMargin

    // Insert the new group
    testGroupService
      .add(testGroup2Model)
      .map(gm => gm: GroupModel) match {
          case Some(gm) => Logger.info(s"Inserted test group, id=${gm.id.value}")
          case None => Logger.error("Failed to insert test model!")
      }

    val testUser1AsJson = Json.parse(testUser1)
    val request = FakeRequest(POST, s"/groups/$testGroup2Id/users", FakeHeaders(), testUser1AsJson)
    val method = controller.associateNewUser(testGroup2Id)(request)
    val content = contentAsJson(method)(Timeout(20.seconds))

    s"return $CREATED" in {
      status(method)(Timeout(20.seconds)) mustBe CREATED
    }

    "return jsonapi content" in {
      // TODO Test jsonapi content
      (content \ "data" \ "attributes" \ "user")
        .validate[String]
        .fold(
          _ => assert(false),
          g => assert(g == "test-user-1")
        )
    }
  }


  "Group Controller, finding groups" should {

    val request = FakeRequest(GET, s"/groups")
    val method = controller.index()(request)

    s"return $OK" in {
      status(method)(Timeout(20.seconds)) mustBe OK
    }

    s"return content" in {
      val content = contentAsJson(method)(Timeout(20.seconds))
      (content \ "data")
        .validate[JsArray]
        .fold(
          _ => assert(false),
          g => assert(g.value.nonEmpty)
        )
    }

    "find a group by its id" in {
      // TODO
    }

    "show all the users associated with a group" in {
      // TODO
    }

  }


  "Group Controller, garbage in, garbage out" should {

    // invalid type: user
    val invalidTestGroup1 =
      """
        |{
        |	"data": {
        |		"type":"user",
        |		"id":"",
        |		"attributes":{
        |			"group":"test-group-1"
        |		}
        |	}
        |}
      """.stripMargin

    val invalidTestGroup1AsJson = Json.parse(invalidTestGroup1)
    val request = FakeRequest(POST, s"/group", FakeHeaders(), invalidTestGroup1AsJson)
    val method = controller.create()(request)

    s"return $BAD_REQUEST" in {
      status(method)(Timeout(30.seconds)) mustBe BAD_REQUEST
    }

  }

}
