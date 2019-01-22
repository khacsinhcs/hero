package actors

object events {

  case class CreateEvent[Type <: Sendable](t: Type)

  case class UpdateEvent[Key, Type <: Sendable](key: Key, t: Type)

  case class DeleteEvent[Key](key: Key)

  case class GetAll[Type <: Sendable]()

  case class Get[Key](key: Key)

}

trait Sendable
