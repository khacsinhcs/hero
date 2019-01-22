package controllers

import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.google.inject.name.Named
import javax.inject.Inject
import play.api.cache.redis.CacheAsyncApi
import play.api.libs.json._
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class HostController @Inject()(cc: ControllerComponents, cache: CacheAsyncApi, @Named("hostActor") hostActor: ActorRef)(implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  import model.HostConfig._
  import actors.events._
  import akka.pattern.{ask}

  implicit val timeout = Timeout(5 seconds)

  def getHost(name: String) = Action.async {
    ask(hostActor, Get(name)).mapTo[Host].map(host => Ok(Json.toJson(host)))
  }

  def createHost = Action { request: Request[AnyContent] =>
    request.body.asJson.map { json =>
      json.validate[Host] asOpt match {
        case Some(host) =>
          hostActor ! CreateEvent(host)
          Ok("")
        case None => BadRequest
      }
    } getOrElse {
      BadRequest("Expecting application/json request body")
    }
  }

  def updateHost(name: String) = ???

  def deleteHost(name: String) = ???

  def getAllHosts = ???
}
