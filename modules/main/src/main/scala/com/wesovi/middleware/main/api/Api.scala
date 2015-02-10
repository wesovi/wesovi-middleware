package com.wesovi.middleware.main.api

import akka.actor.{ActorLogging, Props, actorRef2Scala}
import akka.io.IO
import com.wesovi.middleware.administrator.api.route.AdministratorRoute
import com.wesovi.middleware.common.api.util.{CorsSupport, WesoviExceptionHandler, WesoviRejectionHandler}
import com.wesovi.middleware.common.util.ConfigHolder
import com.wesovi.middleware.main.{CoreActors, Core}
import com.wesovi.middleware.system.api.route.SystemRoute
import spray.can.Http
import spray.routing.Directive.pimpApply
import spray.routing._
import spray.util.LoggingContext

import scala.concurrent.ExecutionContext.Implicits.global


trait Api extends Directives with RouteConcatenation  with ConfigHolder {
  this: CoreActors with Core =>

  val routes ={

    pathPrefix("api" / "v1") {
        new SystemRoute().route ~
        new AdministratorRoute().route
    }

  }

  val rootService = system.actorOf(ApiService.props(getHostname, getPort, routes))

}

object ApiService {

  def props(hostname: String, port: Int, routes: Route) = Props(classOf[ApiService], hostname, port, routes)
}


class ApiService(hostname: String, port: Int, route: Route) extends HttpServiceActor with ActorLogging with CorsSupport {

  IO(Http)(context.system) ! Http.Bind(self, hostname, port)

  override implicit def actorRefFactory = context

  println("BBB")
  implicit val rh = WesoviRejectionHandler.Default

  def receive: Receive = {

    runRoute(route)(WesoviExceptionHandler.default, rh, context,
      RoutingSettings.default, LoggingContext.NoLogging)
  }

}

