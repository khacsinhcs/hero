package actors

import akka.actor.Actor
import events._
trait CacheBaseActor[Key, Type] extends Actor {
  override def receive: Receive = {
    case CreateEvent[Type](t)
  }
}
