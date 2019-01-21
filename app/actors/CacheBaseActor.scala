package actors

import actors.events._
import akka.Done
import akka.actor.Actor
import javax.xml.crypto.dsig.keyinfo.KeyName
import play.api.cache.redis.CacheAsyncApi

import scala.concurrent.ExecutionContext

trait CacheBaseActor[Key, Type] extends Actor {
  val cache: CacheAsyncApi

  implicit val executionContext: ExecutionContext
  import scala.concurrent.duration._

  override def receive: Receive = {
    case CreateEvent(t: Type) =>
      val keyName: Key = keyOf(t)
      cache.set(keyCode(keyName), t, 1000.minutes)
      cache.set[String](s"Type($typeName)") add keyCode(keyName)
    case UpdateEvent(keyName: Key, data: Key) =>
      cache.get[Type](keyCode(keyName)).foreach {
        case Some(_) => cache.set(keyCode(keyName), data)
        case None => throw new RuntimeException("Something wrong")
      }
    case DeleteEvent(keyName: Key) =>
      cache.get[Type](keyCode(keyName)).foreach {
        case Some(_) =>
          cache.remove(keyCode(keyName))
          cache.set[String](s"Type($typeName)").remove(keyCode(keyName))
        case None => throw new RuntimeException("Something wrong")
      }
    case GetAll =>
      ???
  }

  private def keyCode(keyName: Key) = {
    typeName + "(" + keyName + ")"
  }

  val typeName: String

  def keyOf(t: Type): Key
}


