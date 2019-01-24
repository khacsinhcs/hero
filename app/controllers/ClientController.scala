package controllers

import actors.ClientActor
import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.alab.mvc.{AsyncCreateAction, Fail, MvcHelper, Success}
import javax.inject.Inject
import play.api.cache.redis.CacheApi
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._


class ClientController  @Inject()(cache: CacheApi, actorSystem: ActorSystem)(implicit cc: ControllerComponents, implicit val executionContext: ExecutionContext) extends AbstractController(cc) {

  import com.alab.mvc.events._
  import akka.pattern.ask
  import model.CustomerConfig._
  implicit val timeout: Timeout = Timeout(5 seconds)

  val clientActor: ActorRef = actorSystem.actorOf(ClientActor.prop(executionContext, cache))

  def get(name: String): Action[AnyContent] = Action.async {
    (clientActor ? Get(name)).mapTo[Option[Client]] map {
      case Some(client) => Ok(Json.toJson(client))
      case None => NotFound
    }
  }

  def create: Action[AnyContent] = MvcHelper simpleCreate[Client] clientActor

}