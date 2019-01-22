package controllers

import actors.HostActor
import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import javax.inject.Inject
import play.api.cache.redis.CacheAsyncApi
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class HostController @Inject()(cc: ControllerComponents, cache: CacheAsyncApi, actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  import actors.events._
  import akka.pattern.ask
  import model.HostConfig._

  implicit val timeout: Timeout = Timeout(5 seconds)

  val hostActor: ActorRef = actorSystem.actorOf(HostActor.prop(executionContext, cache))

  def getHost(name: String): Action[AnyContent] = Action.async {
    ask(hostActor, Get(name)).mapTo[Host].map(host => Ok(Json.toJson(host)))
  }

  def createHost = Action { request: Request[AnyContent] =>
    request.body.asJson map { json =>
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

  def updateHost(name: String): Action[AnyContent] = Action { _: Request[AnyContent] => Ok("Todo") }

  def deleteHost(name: String): Action[AnyContent] = Action.async { _: Request[AnyContent] =>
    ask(hostActor, DeleteEvent(name)).map(_ => Ok)
  }

  def getAllHosts: Action[AnyContent] = Action.async { _: Request[AnyContent] =>
    ask(hostActor, GetAll[Host]()).mapTo[Seq[Host]].map(hosts => Ok(Json.toJson(hosts)))
  }
}
