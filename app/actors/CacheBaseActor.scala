package actors

import actors.events._
import akka.actor.Actor
import play.api.cache.redis.CacheAsyncApi

import scala.concurrent.{Await, ExecutionContext}
import scala.reflect.ClassTag

abstract class CacheBaseActor[Key : ClassTag, Type <: Sendable : ClassTag] extends Actor {
  val cache: CacheAsyncApi

  implicit val executionContext: ExecutionContext

  import scala.concurrent.duration._
  import scala.util.Success

  override def receive: Receive = {

    case CreateEvent(t: Type) =>
      val keyName: Key = keyOf(t)
      cache set(keyCode(keyName), t, 1000 minutes)
      cache set[String] s"Type($typeName)" add keyCode(keyName)
    case UpdateEvent(keyName: Key, data: Type) =>
      cache.get[Type](keyCode(keyName)).foreach {
        case Some(_) => cache.set(keyCode(keyName), data)
      }
    case DeleteEvent(keyName: Key) =>
      cache get[Type] keyCode(keyName) map {
        case Some(_) =>
          cache.remove(keyCode(keyName))
          cache.set[String](s"Type($typeName)").remove(keyCode(keyName))
      } onComplete {
        case Success(_) => sender() ! true
      }
    case GetAll =>
      cache get[Set[Type]] s"Type($typeName)" map {
        case Some(s) => s
        case None => Set()
      } onComplete {
        case Success(value) => sender() ! value
      }

    case Get(keyName: Key) =>
      val get = cache get[Option[Type]] keyCode(keyName)
      val result = Await.result(get, 5 seconds)
      sender ! result
  }

  protected def keyCode(keyName: Key) = {
    typeName + "(" + keyName + ")"
  }

  val typeName: String

  def keyOf: Type => Key
}


