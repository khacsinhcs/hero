package controllers

import actors.ClientActor
import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.alab.mvc.action.BodyAsJson
import javax.inject.Inject
import play.api.cache.redis.CacheApi
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ClientController @Inject()(cache: CacheApi, actorSystem: ActorSystem, bodyAsJson: BodyAsJson)(implicit cc: ControllerComponents, executionContext: ExecutionContext, bodyParsers: BodyParsers.Default)
  extends AbstractController(cc) {

  import akka.pattern.ask
  import com.alab.mvc.action._
  import com.alab.mvc.events._
  import model.CustomerConfig._

  implicit val timeout: Timeout = Timeout(5 seconds)

  val clientActor: ActorRef = actorSystem.actorOf(ClientActor.prop(executionContext, cache))

  def get(name: String): Action[AnyContent] = Action.async {
    (clientActor ? Get(name)).mapTo[Option[Client]] map {
      case Some(client) => Ok(Json.toJson(client))
      case None => NotFound
    }
  }

  def create: Action[AnyContent] = bodyAsJson { r: RequestAsJson[_] =>
    r.json.is("Client")
    Ok("")
  }

}
