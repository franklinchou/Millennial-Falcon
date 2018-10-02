import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

import lib.StringContainer

package object models {

  val DefaultTime: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)

  val Id = "id"
  val Name = "name"
  val Type = "model-type"
  val CreatedAt = "created-at"
  val ModifiedAt = "modified-at"

  val UserType = "user"
  val GroupType = "group"
  val FeatureType = "product"

  // Edges


  def generateUUID[M <: AnyVal]: StringContainer[M] = {
    val uuid = UUID.randomUUID()
    StringContainer.apply[M](uuid.toString)
  }

}
