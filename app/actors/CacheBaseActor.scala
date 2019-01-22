package actors

import actors.events._
import akka.actor.Actor
import play.api.cache.redis.CacheAsyncApi

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

trait CacheBaseActor[Key, Type <: Sendable] extends Actor {
  val cache: CacheAsyncApi

  implicit val executionContext: ExecutionContext

  import scala.concurrent.duration._
  import scala.util.Success

  override def receive: Receive = {

    case CreateEvent(t: Type) =>
      val keyName: Key = keyOf(t)
      cache.set(keyCode(keyName), t, 1000.minutes)
      cache.set[String](s"Type($typeName)") add keyCode(keyName)
    case UpdateEvent(keyName: Key, data: Type) =>
      cache.get[ClassTag[Type]](keyCode(keyName)).foreach {
        case Some(_) => cache.set(keyCode(keyName), data)
      }
    case DeleteEvent(keyName: Key) =>
      cache get[ClassTag[Type]] keyCode(keyName) map {
        case Some(_) =>
          cache.remove(keyCode(keyName))
          cache.set[String](s"Type($typeName)").remove(keyCode(keyName))
      } onComplete {
        case Success(_) => sender() ! true
      }
    case GetAll =>
      cache.get[Set[ClassTag[Type]]](s"Type($typeName)") map {
        case Some(s) => s
        case None => Set()
      } onComplete {
        case Success(value) => sender() ! value
      }

    case Get(keyName: Key) =>
      cache get[ClassTag[Type]] keyCode(keyName) onComplete {
        case Success(Some(t)) => sender() ! Some(t)
        case _ => sender() ! None
      }
  }

  private def keyCode(keyName: Key) = {
    typeName + "(" + keyName + ")"
  }

  val typeName: String

  def keyOf: Type => Key
}


