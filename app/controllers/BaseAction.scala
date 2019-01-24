package controllers

import play.api.libs.json.{Reads, Writes}
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

trait BaseAction {
  val cc: ControllerComponents
  protected implicit val ex: ExecutionContext = cc.executionContext

  def Action: ActionBuilder[Request, AnyContent] = cc.actionBuilder

}

trait ReadWrite[In, Out] {
  protected implicit val read: Reads[In]
  protected implicit val write: Writes[Out]
}



trait AsyncAction[In, Out] extends BaseAction with ReadWrite[In, Out] {

  def as(job: In => Future[Done[Out]]): Action[AnyContent] = Action async { request: Request[AnyContent] =>
    request.body.asJson map { json =>
      json.validate[In] asOpt match {
        case Some(value) =>
          job(value) map {
            case Success() => Results Ok
            case Fail(msg) => Results BadRequest Json.toJson(msg)
          }
        case None => Future.successful(Results BadRequest "Wrong format")
      }
    } getOrElse {
      Future.successful(Results BadRequest "Expecting application/json request body")
    }

  }

}

protected abstract class ParseJsonAction[Type](implicit val reader: Reads[Type], implicit val cc: ControllerComponents) extends BaseAction {
  def sync(f: Type => Done[String]): Action[AnyContent] = Action { request: Request[AnyContent] =>
    request.body.asJson map { json =>
      json.validate[Type] asOpt match {
        case Some(value) =>
          f(value) match {
            case Success() => Results Ok
            case Fail(msg) => Results BadRequest msg
          }
        case None => Results BadRequest "Wrong format"
      }
    } getOrElse {
      Results.BadRequest("Expecting application/json request body")
    }
  }

}

case class CreateAction[Type](implicit override val reader: Reads[Type], override implicit val cc: ControllerComponents) extends ParseJsonAction[Type]

case class AsyncCreateAction[Type](implicit val write: Writes[String], implicit val read: Reads[Type], implicit val cc: ControllerComponents) extends AsyncAction[Type, String]

case class AsyncUpdateAction[Type](implicit val write: Writes[String], implicit val read: Reads[Type], implicit val cc: ControllerComponents) extends AsyncAction[Type, String]

trait Done[Type]

case class Fail[Type](message: Type) extends Done[Type]

case class Success[Type]() extends Done[Type]
