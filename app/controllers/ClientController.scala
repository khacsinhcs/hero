package controllers

import actors.ClientActor
import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.alab.MappableHelper
import com.alab.model.MapValues
import com.alab.mvc.action.BodyAsJson
import com.alab.mvc.data.Convert
import javax.inject.Inject
import model.config._
import play.api.cache.redis.CacheApi
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
      case Some(c) =>
        val client = MappableHelper.mapify[Client](c)
        Ok(Convert.toJson(MapValues(client), ClientConf))
      case None => NotFound
    }
  }

  def create: Action[AnyContent] = bodyAsJson { r: RequestAsJson[_] =>
    val values = r.json
    val client = values.materialize[Client](ClientConf)
    clientActor ? CreateEvent(client)
    Ok("")
  }

}
