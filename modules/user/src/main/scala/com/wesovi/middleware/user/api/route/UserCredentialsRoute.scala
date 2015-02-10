package com.wesovi.middleware.user.api.route

import akka.actor.ActorRefFactory
import akka.pattern.ask
import akka.util.Timeout
import com.wesovi.middleware.common.api.ApiRoute
import com.wesovi.middleware.common.api.util.ApplicationTokenValidator
import com.wesovi.middleware.common.util.{BadRequestException, PasswordEncoder, PublicIdGenerator, ResourceNotFoundException}
import com.wesovi.middleware.user.ActorLocator
import com.wesovi.middleware.user.api.domain.ChangePasswordDto
import com.wesovi.middleware.user.application.database.User
import com.wesovi.middleware.user.application.database.UserRepositoryMessage.{FindUserById, UpdateUserMessage}
import spray.http.HttpHeaders.RawHeader
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.routing._
import spray.util.LoggingContext

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

/**
 * Created by ivan on 27/1/15.
 */
object UserCredentialsRoute {

}

class UserCredentialsRoute (implicit ec: ExecutionContext, actorRefFactory:ActorRefFactory,log: LoggingContext) extends ApiRoute with ApplicationTokenValidator{

  implicit val timeout = Timeout(10 seconds)

  val userService = ActorLocator.userRepository(actorRefFactory)

  def getUser(userId:Long):Option[User]={
    val getUserByUserIdFuture:Future[Option[User]] = (userService ? FindUserById(userId)).mapTo[Option[User]]
    val user =  Await.result[Option[User]](getUserByUserIdFuture,timeout.duration)
    user
  }

  def updateUser(user:User)={
    val updateUserFuture = userService ? UpdateUserMessage(user)
    updateUserFuture.onSuccess{
      case _ => println("User was updated.")
    }
  }

  val route: Route =

      path("user" / Segment / "password" / "action" / "change") { (userPublicId) =>
        put {
          respondWithMediaType(`application/json`) {
            respondWithHeaders(RawHeader("W-Action", "changePassword")) {
              entity(as[ChangePasswordDto]) {
                request => {
                  requestContext =>
                    val privateId: Long = PublicIdGenerator.decode(userPublicId)
                    request.validate
                    val userOption: Option[User] = getUser(privateId)
                    if (userOption.isEmpty){
                      throw new ResourceNotFoundException("There is not user with this id", None)
                    }
                    val user:User = userOption.get
                    if(!user.password.get.equals(PasswordEncoder.encode(request.oldPassword.get))){
                      throw new BadRequestException("Invalid password", None)
                    }
                    user.password = Some(PasswordEncoder.encode(request.password.get))
                    updateUser(user)
                    requestContext.complete(StatusCodes.NoContent)
                }
              }
            }
          }
        }
      }~
      path("user" / Segment / "password" / "action" / "reset") { (userPublicId) =>
        put {
          respondWithMediaType(`application/json`) {
            respondWithHeaders(RawHeader("W-Action", "resetPassword")) {
              val privateId: Long = PublicIdGenerator.decode(userPublicId)
              val userOption: Option[User] = getUser(privateId)
              if (userOption.isEmpty){
                throw new ResourceNotFoundException("There is not user with this id", None)
              }
              val user:User = userOption.get
              user.password = Some(PasswordEncoder.encode(PublicIdGenerator.randomAlphanumericString(8)))
              updateUser(user)
              complete(StatusCodes.NoContent)
            }
          }
        }
      }

}