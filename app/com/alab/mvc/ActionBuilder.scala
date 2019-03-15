package com.alab.mvc

import com.alab.mvc.request.RequestAsJson
import javax.inject.Inject
import play.api.mvc.{AnyContent, _}

import scala.concurrent.{ExecutionContext, Future}

object actionHelper {

  class AsJson @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
    extends ActionBuilder[RequestAsJson, AnyContent] with ActionTransformer[Request, RequestAsJson] {
    override protected def transform[A](request: Request[A]): Future[RequestAsJson[A]] = {
      request.body match {
        case any: AnyContent => any.asJson match {
          case Some(t) => Future.successful(RequestAsJson[A](request, t))
        }
        case _ => Future.failed(new RuntimeException("Could not handle none Json body"))
      }
    }
  }

}
