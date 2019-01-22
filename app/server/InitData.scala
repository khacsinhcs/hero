package server

import actors.HostActor
import actors.events.CreateEvent
import akka.actor.ActorSystem
import com.google.inject.{Inject, Singleton}
import model.HostConfig._
import model.serverdto._
import play.api.cache.redis.CacheAsyncApi

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Singleton
class InitData @Inject()(cache: CacheAsyncApi, actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) {

  object Database {
    val hosts = List(Host("DEV", "", "", ""), Host("DEV2", "", "", ""))
    val clients = List(Client("securityfeature", ""))
  }

  def initData(): Unit = {
    val actor = actorSystem.actorOf(HostActor.prop(executionContext, cache))
    Database.hosts foreach(host => actor ! CreateEvent(host))
  }

  initData()
}
