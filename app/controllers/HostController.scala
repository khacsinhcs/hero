package controllers

import actors.HostActor
import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.alab.mvc._
import com.alab.mvc.actionHelper._
import javax.inject.Inject
import play.api.cache.redis.CacheApi
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class HostController @Inject()(cache: CacheApi, actorSystem: ActorSystem, asJson: AsJson)(implicit cc: ControllerComponents, executionContext: ExecutionContext) extends AbstractController(cc) {

  import akka.pattern.ask
  import events._
  import model.HostConfig._

  implicit val timeout: Timeout = Timeout(5 seconds)

  val hostActor: ActorRef = actorSystem.actorOf(HostActor.prop(executionContext, cache))

  def getHost(name: String): Action[AnyContent] = Action.async {
    ask(hostActor, Get(name)).mapTo[Option[Host]].map {
      case Some(host) => Ok(Json.toJson(host))
      case None => NotFound
    }
  }

  def createHost: Action[AnyContent] = MvcHelper.validateThenCreate[Host](hostActor) {
    case Host(t, _, _, _) if t.isEmpty => Fail("Key Name is required")
    case Host(_, x, y, z) if !x.startsWith("http") && !y.startsWith("http") && !z.startsWith("http") => Fail("Wrong link")
    case _ => Success()
  }

  def test = (asJson andThen asJson andThen asJson) { x =>
    Ok("")
  }

  def updateHost(name: String): Action[AnyContent] = new AsyncUpdateAction[Host] as { host =>
    if (name != host.name)
      Future.successful(Fail(s"Can't change key"))
    else
      (hostActor ? UpdateEvent(name, host)).mapTo[Boolean] map (result => if (result) Success() else Fail("Update fail"))
  }

  def deleteHost(name: String): Action[AnyContent] = Action.async { _: Request[AnyContent] =>
    val isSuccess = hostActor ? DeleteEvent(name)
    isSuccess.mapTo[Boolean] map (b => if (b) Ok("") else NotFound(""))
  }

  def getAllHosts: Action[AnyContent] = Action.async { _: Request[AnyContent] =>
    ask(hostActor, GetAll()).mapTo[List[Host]] map (hosts => Ok(Json.toJson(hosts)))
  }
}
