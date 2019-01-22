package server

import com.google.inject.{Inject, Singleton}
import play.api.cache.redis.CacheApi

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import model.HostConfig._
import model.serverdto._

@Singleton
class InitData @Inject()(cache: CacheApi)(implicit executionContext: ExecutionContext) {

  object Database {
    val hosts = List(Host("DEV", "", "", ""), Host("DEV2", "", "", ""))
    val clients = List(Client("securityfeature", ""))
  }

  def initData(): Unit = {
    Database.hosts.map(host => ("Host#" + host.name, host)).foreach(host => {
      cache.set(host._1, host._2, 10 minutes)
      cache.append("hostKeys", host._1)
    })
  }

  initData()
}
