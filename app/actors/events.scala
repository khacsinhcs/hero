package actors

object events {

  case class CreateEvent[Type](t: Type)

  case class UpdateEvent[Key, Type](key: Key, t: Type)

  case class DeleteEvent[Key](key: Key)

  case class GetAll[Type]()

  case class Get[Key](key: Key)

}
