package com.alab.mvc

import events.CreateEvent
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.ControllerComponents

import scala.concurrent.{ExecutionContext, Future}

object MvcHelper {

  def validateThenCreate[Type <: Sendable](createActor: ActorRef)(validate: Type => Done[String])(implicit timeHost: Timeout, write: Writes[String], read: Reads[Type], cc: ControllerComponents, ex: ExecutionContext) =
    new AsyncCreateAction[Type] as { candidate: Type =>
      validate(candidate) match {
        case Success() => (createActor ? CreateEvent[Type](candidate)).mapTo[Boolean] map (result => if (result) Success() else Fail("Save fail"))
        case Fail(msg: String) => Future.successful(Fail(msg))
      }
    }

  def simpleCreate[Type <: Sendable](createActor: ActorRef)(implicit timeHost: Timeout, write: Writes[String], read: Reads[Type], cc: ControllerComponents, ex: ExecutionContext) =
    validateThenCreate[Type](createActor) { _ => Success() }(timeHost, write, read, cc, ex)

}