package com.alab.mvc

import play.api.libs.json.JsValue
import play.api.mvc._


object request {
  case class RequestAsJson[A](request: Request[A], json: JsValue) extends WrappedRequest[A](request)

  case class HasCaseClass[A, Data](request: RequestAsJson[A], data: Data) extends WrappedRequest[A](request)

}