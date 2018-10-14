import gremlin.scala.Vertex
import lib.jsonapi.DocumentSingle
import play.api.libs.json.{JsValue, Json}
import models.vertex.GroupModel._
import models.vertex.UserModel._
import resources.{GroupResource, UserResource}

package object controllers {

  def jsonifyUserGroup(user: Vertex, group: Vertex): JsValue = {
    val resource = UserResource(user)
    val groupResource = GroupResource(group)
    val document = DocumentSingle(resource, Seq(groupResource))
    Json.toJson(document)
  }

}
