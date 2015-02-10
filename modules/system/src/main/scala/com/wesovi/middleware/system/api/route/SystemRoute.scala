package com.wesovi.middleware.system.api.route

import akka.actor.ActorRefFactory
import akka.util.Timeout
import com.wesovi.middleware.common.api.ApiRoute
import spray.http.{MediaTypes, StatusCodes}
import spray.routing.Directive.pimpApply
import spray.routing._
import spray.util.LoggingContext

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object SystemRoute {

}

trait SystemRouteDefinition extends ApiRoute{

  println("CCCCC")
  implicit val timeout = Timeout(10 seconds)

  val route: Route =
    path("system" / "status"){
      get{
        respondWithMediaType(MediaTypes.`application/json`) {
          complete(StatusCodes.Accepted, "ok")
        }
      }
    }
}

class SystemRoute (implicit ec: ExecutionContext, actorRefFactory:ActorRefFactory,log: LoggingContext) extends SystemRouteDefinition{

}

