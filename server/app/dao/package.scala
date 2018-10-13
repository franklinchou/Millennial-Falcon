import models.vertex

package object dao {

  // TODO Should be of type StringContainer[Key]
  val keys: Set[String] = {
    Set[String](
      vertex.UserType,
      vertex.GroupType,
      vertex.FeatureType
    )
  }

  val byIdComposite = "id-index"

  val byTypeComposite = "type-index"

  val byIdTypeComposite = "id-type-index"

  val byTypeNameComposite = "type-name-index"

  // Find a user-group by name
  val byGroupNameComposite = "group-name-index"

  val byUserNameComposite = "user-name-index"

  val byFeatureNameComposite = "feature-name-index"


  val indices: Set[String] = {
    Set[String](
      byIdComposite,
      byTypeComposite,
      byIdTypeComposite,
      byTypeNameComposite,
      byGroupNameComposite,
      byUserNameComposite,
      byFeatureNameComposite
    )
  }

}
