package actors

import actors.events._
import akka.actor.Actor
import model.HostConfig.Host
import play.api.cache.redis.CacheAsyncApi

import scala.concurrent.ExecutionContext
import scala.util.Try

trait CacheBaseActor[Key, Type] extends Actor {
  val cache: CacheAsyncApi

  implicit val executionContext: ExecutionContext

  import scala.concurrent.duration._
  import scala.util.{Success, Failure}

  override def receive: Receive = {

    case CreateEvent(t: Type) =>
      val keyName: Key = keyOf(t)
      cache.set(keyCode(keyName), t, 1000.minutes)
      cache.set[String](s"Type($typeName)") add keyCode(keyName)
    case UpdateEvent(keyName: Key, data: Key) =>
      cache.get[Type](keyCode(keyName)).foreach {
        case Some(_) => cache.set(keyCode(keyName), data)
      }
    case DeleteEvent(keyName: Key) =>
      cache.get[Type](keyCode(keyName)) map {
        case Some(_) =>
          cache.remove(keyCode(keyName))
          cache.set[String](s"Type($typeName)").remove(keyCode(keyName))
      } onComplete {
        case Success(_) => sender() ! true
      }
    case GetAll =>
      cache.get[Set[Host]](s"Type($typeName)") map {
        case Some(s) => s
        case None => Set()
      } onComplete {
        case Success(value) => sender() ! value
      }
  }

  private def keyCode(keyName: Key) = {
    typeName + "(" + keyName + ")"
  }

  val typeName: String

  def keyOf(t: Type): Key
}


