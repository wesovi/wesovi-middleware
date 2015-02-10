package com.wesovi.middleware.common.api


import com.wesovi.middleware.common.api.util.CorsSupport
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing._
import spray.util.LoggingContext




object ApiRoute {
  case class Message(message: String)

  object ApiRouteProtocol extends DefaultJsonProtocol {
    implicit val messageFormat = jsonFormat1(Message)
  }

  object ApiMessages {
    val UnknownException = "Unknown exception"
    val UnsupportedService = "Sorry, provided service is not supported."
  }

}

abstract class ApiRoute(implicit log: LoggingContext) extends Directives with SprayJsonSupport with CorsSupport{


}

