package object dao {

  val User = "user"
  val UserGroup = "user-group"  // equivalent to "client"


  // TODO Should be of type StringContainer[Key]
  val keys: Set[String] = {
    Set[String](
      User,
      UserGroup
    )
  }

}
