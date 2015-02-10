package com.wesovi.middleware.common.api.util

import com.typesafe.config.ConfigFactory
import com.wesovi.middleware.common.api.ApiRoute
import spray.routing.Directive1

/**
 * Created by ivan on 22/1/15.
 */

case class ApplicationToken(value:Option[String]=None,val channel:Option[String]=Some("test"))

trait ApplicationTokenValidator extends ApiRoute{

  val conf = ConfigFactory.load()

  lazy val validApplicationToken = conf.getString("application.token")

  def validateApplicationToken:Directive1[ApplicationToken]={
    var applicationToken:Option[ApplicationToken]=None
    optionalHeaderValueByName("W-ApplicationToken").flatMap {
      case Some(applicationTokenValue)=>
      {
        applicationToken = checkApplicationToken (applicationTokenValue)
        if (applicationToken.isDefined)
          provide (applicationToken.get)
        else
            reject(ApplicationTokenRejection("Invalid application token"))
      }
      case None => reject(ApplicationTokenRejection("Required application token"))
    }



  }

  private def checkApplicationToken(applicationTokenValue:String):Option[ApplicationToken] = {
    if( applicationTokenValue.equals(validApplicationToken))
      Some(ApplicationToken(value=Some(applicationTokenValue),channel = Some("verified")))
    else
      None
  }

  private def transformApplicationToken(applicationToken:ApplicationToken):ApplicationToken={
    applicationToken.copy(channel=Some("verified"))
  }
}
