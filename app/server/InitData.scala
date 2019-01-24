package server

import actors.{ClientActor, HostActor}
import actors.events.CreateEvent
import akka.actor.ActorSystem
import com.google.inject.{Inject, Singleton}
import model.CustomerConfig.Client
import model.HostConfig._
import play.api.cache.redis.CacheApi

import scala.concurrent.ExecutionContext

@Singleton
class InitData @Inject()(cache: CacheApi, actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) {

  object Database {
    val hosts = List(Host("DEV", "dev", "dev", "dev"), Host("DEV2", "dev2", "dev2", "dev2"))
    val clients = List(Client("securityfeature", "securityfeature"))
  }

  def initData(): Unit = {
    val hostActor = actorSystem.actorOf(HostActor.prop(executionContext, cache))
    val clientActor = actorSystem.actorOf(ClientActor.prop(executionContext, cache))

    Database.hosts foreach(host => hostActor ! CreateEvent(host))
    Database.clients foreach(client => clientActor ! CreateEvent(client))
  }

  initData()
}
