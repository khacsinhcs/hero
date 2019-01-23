package actors

import actors.events._
import akka.actor.Actor
import play.api.cache.redis.CacheApi

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

abstract class CacheBaseActor[Key: ClassTag, Type <: Sendable : ClassTag] extends Actor {
  val cache: CacheApi

  implicit val executionContext: ExecutionContext

  override def receive: Receive = {

    case CreateEvent(t: Type) =>
      val keyName: Key = keyOf(t)
      cache set(keyCode(keyName), t)
      cache set[String] s"Type($typeName)" add keyCode(keyName)
    case UpdateEvent(keyName: Key, data: Type) =>
      val oldValue = cache get[Type] keyCode(keyName)
      val isSuccess = oldValue match {
        case Some(_) =>
          cache set (keyCode(keyName), data)
          true
        case None => false
      }
      sender ! isSuccess
    case DeleteEvent(keyName: Key) =>
      val success = cache get[Type] keyCode(keyName)
      cache.remove(keyCode(keyName))
      cache.set[String](s"Type($typeName)").remove(keyCode(keyName))
      sender ! success
    case GetAll() =>
      val keys = cache.set[String](s"Type($typeName)").toSet
      sender ! cache.getAll[Type](keys).filter(option => option.isDefined).map(data => data.get).toList
    case Get(keyName: Key) =>
      sender ! (cache get[Option[Type]] keyCode(keyName))
  }

  protected def keyCode(keyName: Key): String = s"$typeName($keyName)"

  val typeName: String

  def keyOf: Type => Key
}


