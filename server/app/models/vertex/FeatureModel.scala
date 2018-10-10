package models.vertex

import java.time.ZonedDateTime

import lib.StringContainer
import models.field.{FeatureField, IdField}
import models.vertex
import org.apache.tinkerpop.gremlin.structure.Vertex
import play.api.libs.json.{Json, OWrites}

object FeatureModel {

  implicit lazy val jsWriter: OWrites[FeatureModel] = (fm: FeatureModel) => {
    Json.obj(
      "type" -> fm.`type`,
      "id" -> fm.id.value,
      "attributes" -> Json.obj(
        "feature" -> fm.name,
        "created-at" -> fm.createdAt.toString,
        "modified-at" -> fm.modifiedAt.toString
      )
    )
  }

  /**
    * Marshall a feature vertex to [[FeatureModel]]
    *
    * @param v
    * @return
    */
  implicit def vertex2FeatureModel(v: Vertex): FeatureModel = {
    val id = v.property(vertex.Id).value.toString
    val feature = v.property(vertex.Name).value.toString
    val createdAt = v.property(vertex.CreatedAt).value.toString
    val modifiedAt = v.property(vertex.ModifiedAt).value.toString

    FeatureModel
      .apply(
        id = StringContainer.apply[IdField](id),
        name = StringContainer.apply[FeatureField](feature),
        createdAt = ZonedDateTime.parse(createdAt),
        modifiedAt = ZonedDateTime.parse(modifiedAt)
      )
  }


  def apply(feature: StringContainer[FeatureField]): FeatureModel = {
    FeatureModel(
      id = generateUUID[IdField],
      name = feature,
      createdAt =  DefaultTime,
      modifiedAt = DefaultTime
    )
  }


}


case class FeatureModel(id: StringContainer[IdField],
                        name: StringContainer[FeatureField],
                        createdAt: ZonedDateTime,
                        modifiedAt: ZonedDateTime) extends Model[FeatureField] {

  val `type`: String = vertex.FeatureType

}