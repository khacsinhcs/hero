package controllers

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.name.Named
import javax.inject.Inject
import play.api.cache.redis.CacheAsyncApi
import play.api.libs.json._
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class HostController @Inject()(cc: ControllerComponents, cache: CacheAsyncApi, @Named("hostActor") hostActor: ActorRef)(implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  import model.HostConfig._

  def getHost(name: String) = Action.async {
    cache.get[Host]("Host#" + name).map(host => Ok(Json.toJson(host)))
  }

  def createHost = Action.async { request: Request[AnyContent] =>
    request.body.asJson.map { json =>
      json.validate[Host] asOpt match {
        case Some(host) =>
          cache.list[String]("HostKey") += "hello me"
          cache.set(host.name, host, 10.hours).map(_ => Ok())
        case None => Future {
          BadRequest
        }
      }
    }.getOrElse {
      Future {
        BadRequest("Expecting application/json request body")
      }
    }
  }

  def updateHost(name: String) = ???

  def deleteHost(name: String) = ???

  def getAllHosts = ???
}
