package user {

  case class Credential(username: String, password: String) extends Serializable

  case class User(username: String,
                   firstName: Option[String], lastName: Option[String],
                   credential: Option[Credential],
                   email: String
  ) extends Serializable

}
