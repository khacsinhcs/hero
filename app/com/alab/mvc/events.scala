package com.alab.mvc

object events {

  case class CreateEvent[Type <: Data](t: Type)

  case class UpdateEvent[Key, Type <: Data](key: Key, t: Type)

  case class DeleteEvent[Key](key: Key)

  case class GetAll()

  case class Get[Key](key: Key)

}

trait Data
