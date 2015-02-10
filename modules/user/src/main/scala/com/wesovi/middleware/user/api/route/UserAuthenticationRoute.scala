package com.wesovi.middleware.user.api.route

import akka.actor.ActorRefFactory
import akka.util.Timeout
import com.wesovi.middleware.common.api.ApiRoute
import com.wesovi.middleware.common.api.util.ApplicationTokenValidator
import com.wesovi.middleware.common.util.{PasswordEncoder, ResourceNotFoundException}
import com.wesovi.middleware.user.ActorLocator
import com.wesovi.middleware.user.api.domain.UserAuthenticationDto
import com.wesovi.middleware.user.application.database.User
import com.wesovi.middleware.user.application.database.UserRepositoryMessage.UserAuthentication
import spray.http.HttpHeaders.RawHeader
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.routing._
import spray.util.LoggingContext
import akka.pattern.ask
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by ivan on 28/1/15.
  */
object UserAuthenticationRoute {

 }

class UserAuthenticationRoute(implicit ec: ExecutionContext, actorRefFactory:ActorRefFactory,log: LoggingContext) extends ApiRoute with ApplicationTokenValidator {

   implicit val timeout = Timeout(10 seconds)

   val userService = ActorLocator.userRepository(actorRefFactory)

   def authenticate(email: String,password:String): Option[User] = {
     val encodedPassword:String = PasswordEncoder.encode(password)
     val getUserByUserIdFuture: Future[Option[User]] = (userService ? UserAuthentication(email,encodedPassword)).mapTo[Option[User]]
     val user = Await.result[Option[User]](getUserByUserIdFuture, timeout.duration)
     user
   }

   val route: Route =

       path("user" / "auth"){
         post {
           respondWithMediaType(`application/json`) {
             respondWithHeaders(RawHeader("W-Action", "authenticateUser")) {
               entity(as[UserAuthenticationDto]) {
                 request => {
                   requestContext =>
                     request.validate
                     val userOption: Option[User] = authenticate(request.email,request.password)
                     if (userOption.isEmpty) {
                       throw new ResourceNotFoundException("There is not user with this credential", None)
                     }
                     requestContext.complete(StatusCodes.NoContent)
                 }
               }
             }
           }
         }
       }

 }