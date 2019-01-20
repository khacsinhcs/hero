package controllers

import javax.inject.Inject
import model.serverdto.Host
import play.api.cache.redis.CacheAsyncApi
import play.api.libs.json._
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class HostController @Inject()(cc: ControllerComponents, cache: CacheAsyncApi)(implicit executionContext: ExecutionContext) extends AbstractController(cc) {
  def getHost(name: String) = Action.async {
    cache.get[Host]("Host#" + name).map(host => Ok(Json.toJson(host)))
  }

  def createHost = Action.async { request: Request[AnyContent] =>
    request.body.asJson.map { json =>
      val host = Host((json \ "name").as[String], (json \ "gateway").as[String], (json \ "web").as[String], (json \ "hello").as[String])
      cache.set(host.name, host, 10.hours).map(_ =>  Ok("Got: " + (json \ "name").as[String]))
    }.getOrElse {
      Future{BadRequest("Expecting application/json request body")}
    }
  }
  def updateHost(name: String) = ???

  def deleteHost(name: String) = ???

  def getAllHosts = ???
}
