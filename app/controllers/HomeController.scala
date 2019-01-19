package controllers

import javax.inject._
import play.api.cache.redis.CacheAsyncApi
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

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


  private def message = cache.getOrElse("hello-world#message", expiration = 10.seconds) {
    "This is a sample message."
  }

  def getMessage = Action.async {
    message.map(msg => Ok(msg))
  }
}
