package com.wesovi.middleware.administrator.api.route

import akka.actor.ActorRefFactory
import com.wesovi.middleware.administrator.api.domain.{AuthTokenDto, AdminDto}
import com.wesovi.middleware.common.api.ApiRoute
import com.wesovi.middleware.common.api.util.ApplicationTokenValidator
import com.wesovi.middleware.common.util.{ConfigHolder, PasswordEncoder, UnAuthorizedUserException}
import spray.http.HttpHeaders.RawHeader
import spray.http.MediaTypes._
import spray.http._
import spray.routing._
import spray.util.LoggingContext

import scala.concurrent.ExecutionContext

/**
 * Created by ivan on 2/2/15.
 */

class AdministratorRoute (implicit ec: ExecutionContext, actorRefFactory:ActorRefFactory,log: LoggingContext) extends ApiRoute with ApplicationTokenValidator with ConfigHolder{



  def validateAdminCredentials(username:String,password:String):AuthTokenDto={
    val expectedUsername = getAdminUsername()
    val expectedPassword = getAdminPassword()
    println(PasswordEncoder.encode(password))
    if(!expectedUsername.equals(username) || !expectedPassword.equals(PasswordEncoder.encode(password)))
      throw new UnAuthorizedUserException("Invalid credentials",None)
    new AuthTokenDto("GGTYY393837575ADs123213",Some(DateTime.now))
  }

  val route: Route =
    validateApplicationToken { applicationToken =>
      path("admin" / "auth"){
        post {
          respondWithMediaType(`application/json`) {
            respondWithHeaders(RawHeader("W-Action", "admin")) {
              entity(as[AdminDto]) {
                request => {
                  requestContext =>
                    request.validate
                    val authToken:AuthTokenDto = validateAdminCredentials(request.username,request.password)
                    val authTokenTxt:String = AuthTokenDto.authTokenFormat.write(authToken).compactPrint
                    requestContext.complete(StatusCodes.OK,HttpEntity(ContentType(MediaTypes.`application/json`, HttpCharsets.`UTF-8`),authTokenTxt))
                  //}
                }
              }
            }
          }
        }
      }
    }
}
