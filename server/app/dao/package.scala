import models.Model

package object dao {

  // TODO Should be of type StringContainer[Key]
  val keys: Set[String] = {
    Set[String](
      Model.UserType,
      Model.GroupType,
      Model.ProductType
    )
  }

  val byIdComposite = "id-index"

  val byTypeComposite = "type-index"

  val byIdTypeComposite = "id-type-index"

  val byTypeNameComposite = "type-name-index"

  // Find a user-group by name
  val byGroupNameComposite = "group-name-index"

  val userNameComposite = "user-name-index"

  val productNameComposite = "product-name-index"


  val indices: Set[String] = {
    Set[String](
      byIdComposite,
      byTypeComposite,
      byIdTypeComposite,
      byTypeNameComposite,
      byGroupNameComposite,
      userNameComposite,
      productNameComposite
    )
  }

}
