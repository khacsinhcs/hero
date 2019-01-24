package actors

import com.alab.mvc.events._
import akka.actor.Actor
import com.alab.mvc.Sendable
import play.api.cache.redis.CacheApi

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

abstract class CacheBaseActor[Key: ClassTag, Type <: Sendable : ClassTag] extends Actor {
  val cache: CacheApi

  implicit val executionContext: ExecutionContext

  override def receive: Receive = {

    case CreateEvent(t: Type) =>
      try {
        val keyName: Key = keyOf(t)
        cache set(keyCode(keyName), t)
        cache set[String] s"Type($typeName)" add keyCode(keyName)
        sender ! true
      } catch {
        case _: Throwable => sender ! false
      }

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
      val existed = cache get[Type] keyCode(keyName)
      existed match {
        case Some(_) =>
          cache.remove(keyCode(keyName))
          cache.set[String](s"Type($typeName)").remove(keyCode(keyName))
          sender ! true
        case None => sender ! false
      }
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


