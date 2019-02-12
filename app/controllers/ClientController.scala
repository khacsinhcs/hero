package controllers

import actors.ClientActor
import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.alab.conf.Type
import com.alab.model.HasValues
import com.alab.mvc.action._
import javax.inject.Inject
import model.config._
import play.api.cache.redis.CacheApi
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ClientController @Inject()(cache: CacheApi, actorSystem: ActorSystem)(implicit cc: ControllerComponents, executionContext: ExecutionContext, bodyParsers: BodyParsers.Default)
  extends AbstractController(cc) with HasValueReader with RequestValidator {

  import akka.pattern.ask
  import com.alab.mvc.action._
  import com.alab.mvc.events._
  import model._

  implicit val timeout: Timeout = Timeout(5 seconds)
  implicit val kind: Type = ClientConf
  implicit val requestFormat: OFormat[Client] = Json.format[Client]

  val clientActor: ActorRef = actorSystem.actorOf(ClientActor.prop(executionContext, cache))

  def get(name: String): Action[AnyContent] = Action.async {
    (clientActor ? Get(name)).mapTo[Option[Client]] map {
      case Some(client) =>
        val clientHasValue = HasValues.from(client)
        Ok(Json.toJson(client))
      case None => NotFound
    }
  }

  def create: Action[AnyContent] = (BodyAsHasValue andThen Validate) { request: RequestAsHasValue[_] =>
    val client = request.hasValues.materialize[Client]
    clientActor ? CreateEvent(client)
    Ok("")
  }

}
