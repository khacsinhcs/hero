package actors

import actors.events._
import akka.actor.Actor
import play.api.cache.redis.CacheApi

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

abstract class CacheBaseActor[Key: ClassTag, Type <: Sendable : ClassTag] extends Actor {
  val cache: CacheApi

  implicit val executionContext: ExecutionContext

  import scala.concurrent.duration._

  override def receive: Receive = {

    case CreateEvent(t: Type) =>
      val keyName: Key = keyOf(t)
      cache set(keyCode(keyName), t, 1000 minutes)
      cache set[String] s"Type($typeName)" add keyCode(keyName)
    case UpdateEvent(keyName: Key, data: Type) =>
      val oldValue = cache get[Type] keyCode(keyName)
      val isSuccess = oldValue match {
        case Some(_) =>
          cache set(keyCode(keyName), data, 1000 minutes)
          true
        case None => false
      }
      sender ! isSuccess
    case DeleteEvent(keyName: Key) =>
      val success = cache get[Type] keyCode(keyName)
      cache.remove(keyCode(keyName))
      cache.set[String](s"Type($typeName)").remove(keyCode(keyName))
      sender ! success
    case GetAll =>
      sender ! (cache get[Set[Type]] s"Type($typeName)")

    case Get(keyName: Key) =>
      sender ! (cache get[Option[Type]] keyCode(keyName))
  }

  protected def keyCode(keyName: Key): String = {
    typeName + "(" + keyName + ")"
  }

  val typeName: String

  def keyOf: Type => Key
}


