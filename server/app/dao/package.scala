import models.Model

package object dao {

  // TODO Should be of type StringContainer[Key]
  val keys: Set[String] = {
    Set[String](
      Model.UserType,
      Model.UserGroupType,
      Model.ProductType
    )
  }

  val idIndex = "id-index"

  val typeIndex = "type-index"

  val idTypeIndex = "id-type-index"

  val typeNameIndex = "type-name-index"

  // Find a user-group by name
  val groupNameIndex = "group-name-index"

  val userNameIndex = "user-name-index"

  val productNameIndex = "product-name-index"


  val indexKeys: Set[String] = {
    Set[String](
      idIndex,
      typeIndex,
      idTypeIndex,
      typeNameIndex,
      groupNameIndex,
      userNameIndex,
      productNameIndex
    )
  }

}
