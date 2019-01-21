package actors

object events {

  case class CreateEvent[Type](t: Type)

  case class UpdateEvent[Type](t: Type)

  case class DeleteEvent[Key](key: Key)

  case class GetAll[Type]()

}
