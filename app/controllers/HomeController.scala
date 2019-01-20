package controllers

import javax.inject._
import play.api.cache.redis.CacheAsyncApi
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import model.serverdto.Host
import play.api.libs.json._
/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, cache: CacheAsyncApi)(implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }


  private def message = cache.get[Host]("Host#DEV")

  def getMessage = Action.async {
    message.map(msg => Ok(Json.toJson(msg)))
  }
}
