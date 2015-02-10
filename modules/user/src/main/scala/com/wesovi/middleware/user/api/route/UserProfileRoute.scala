package com.wesovi.middleware.user.api.route

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.ActorRefFactory
import akka.pattern.ask
import akka.util.Timeout
import com.wesovi.middleware.common.api.ApiRoute
import com.wesovi.middleware.common.api.domain.{PartialUpdate, PartialUpdateType}
import com.wesovi.middleware.common.api.util.ApplicationTokenValidator
import com.wesovi.middleware.common.util.{PublicIdGenerator, ResourceNotFoundException}
import com.wesovi.middleware.user.ActorLocator
import com.wesovi.middleware.user.api.domain.UserProfileDto
import com.wesovi.middleware.user.application.database.PersonRepositoryMessage.{ByUserIdMessage, UpdatePersonMessage}
import com.wesovi.middleware.user.application.database.UserRepositoryMessage.{FindUserById, UpdateUserMessage}
import com.wesovi.middleware.user.application.database.{Person, User, UserStatus}
import spray.http.HttpHeaders.RawHeader
import spray.http.MediaTypes.`application/json`
import spray.http._
import spray.routing._
import spray.util.LoggingContext

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
/**
 * Created by ivan on 24/1/15.
 */
object UserProfileRoute {

}

class UserProfileRoute (implicit ec: ExecutionContext, actorRefFactory:ActorRefFactory,log: LoggingContext) extends ApiRoute with ApplicationTokenValidator{

  val sdf = new SimpleDateFormat("dd/MM/yyyy")

  implicit val timeout = Timeout(10 seconds)

  val personService = ActorLocator.personRepository(actorRefFactory)

  val userService = ActorLocator.userRepository(actorRefFactory)

  def getPerson(userId:Long):Option[Person]={
    val getPersonByUserIdFuture:Future[Option[Person]] = (personService ? ByUserIdMessage(userId)).mapTo[Option[Person]]
    val person = Await.result[Option[Person]](getPersonByUserIdFuture,timeout.duration)
    person
  }

  def getUser(userId:Long):Option[User]={
    val getUserByUserIdFuture:Future[Option[User]] = (userService ? FindUserById(userId)).mapTo[Option[User]]
    val user =  Await.result[Option[User]](getUserByUserIdFuture,timeout.duration)
    user
  }

  def cancelAccount(user:User)={
    user.status=UserStatus.CANCELED
    val updatePersonFuture = userService ? UpdateUserMessage(user)
    updatePersonFuture.onSuccess{
      case _ => println("Person was updated.")
    }

    //getPersonByUserIdFuture onComplete []
    //TODO GET USER AND UPDATE STATUS ALL IN THE FUTURE
  }

  def updateProfile(userId:Long,
                    name:Option[String],
                    gender:Option[String],
                    birthday:Option[Date],
                    city:Option[String],
                    state:Option[String],
                    country:Option[String],
                    zip:Option[String]):Boolean={
    var userFound:Boolean = false
    println("Updating person I "+userId)
    val getPersonByUserIdFuture:Future[Option[Person]] = (personService ? ByUserIdMessage(userId)).mapTo[Option[Person]]
    val currentPerson:Option[Person] = Await.result[Option[Person]](getPersonByUserIdFuture,timeout.duration)
    if(!currentPerson.isEmpty){
      userFound = true
      println("Updating person "+currentPerson.get.id)
      val newPerson = Person(currentPerson.get.id,Some(userId),name,gender,birthday,city,state,country,zip)
      val updatePersonFuture = personService ? UpdatePersonMessage(newPerson)
      updatePersonFuture.onSuccess{
        case _ => println("Person was updated.")
      }
    }
    userFound
  }

  def updateProfile(person:Person)={
    val updatePersonFuture = personService ? UpdatePersonMessage(person)
    updatePersonFuture.onSuccess{
      case _ => println("Person was updated.")
    }
  }

  def replace(person:Person,path:String,value:Option[String]):Person={
    path match{
      case "/name" => person.name=value
    }
    person
  }

  def partialUpdate(person:Person,partialUpdate:PartialUpdate):Person={
    partialUpdate.operation match{
      case PartialUpdateType.REPLACE => replace(person,partialUpdate.path,partialUpdate.value)
      case PartialUpdateType.ADD=>
      case PartialUpdateType.COPY=>
      case PartialUpdateType.REMOVE=>
      case PartialUpdateType.MOVE=>
    }

    person
  }

  def toUserProfile(publicId:String,user:User,person:Person):UserProfileDto={
    new UserProfileDto(publicId, person.name, person.gender, person.birthday, person.city, person.state, person.country, person.zip)
  }


  val route: Route =


          path("user") {
            put {
              respondWithMediaType(`application/json`) {
                respondWithHeaders(RawHeader("W-Action", "updateProfile")) {
                  entity(as[UserProfileDto]) {
                    request => {
                      requestContext =>
                        request.validate
                        println("Updating person PublicId"+request.publicId)
                        val privateId: Long = PublicIdGenerator.decode(request.publicId)
                        println("Updating person PrivateId"+privateId)
                        val result: Boolean = updateProfile(privateId, request.name, request.gender, request.birthday, request.city, request.state, request.country, request.zip)
                        if (!result)
                          throw new ResourceNotFoundException("There is not user with this id", None)
                        val futureResponse = UserProfileDto.userProfileRequestFormat.write(request).compactPrint
                        requestContext.complete(StatusCodes.OK, HttpEntity(ContentType(MediaTypes.`application/json`, HttpCharsets.`UTF-8`), futureResponse))
                    }
                  }
                }
              }
            }
          }~
          path("user" / Segment) { (userPublicId) =>
            get {
              respondWithMediaType(`application/json`) {
                respondWithHeaders(RawHeader("W-Action", "getProfile")) {
                  requestContext =>
                    val privateId: Long = PublicIdGenerator.decode(userPublicId)
                    val user: Option[User] = getUser(privateId)
                    if (user.isEmpty)
                      throw new ResourceNotFoundException("There is not user with this id", None)
                    println("User Status " + user.get.status)
                    val person: Option[Person] = getPerson(privateId)

                    val response = UserProfileDto.userProfileRequestFormat.write(toUserProfile(userPublicId, user.get, person.get)).compactPrint
                    requestContext.complete(StatusCodes.Found, HttpEntity(ContentType(MediaTypes.`application/json`, HttpCharsets.`UTF-8`), response))
                }
              }
            }~
            delete {
              respondWithMediaType(`application/json`) {
                respondWithHeaders(RawHeader("W-Action", "cancelProfile")) {
                  requestContext =>
                    val privateId: Long = PublicIdGenerator.decode(userPublicId)
                    val user: Option[User] = getUser(privateId)
                    if (user.isEmpty)
                      throw new ResourceNotFoundException("There is not user with this id", None)
                    cancelAccount(user.get)
                    println("Removing it")
                    requestContext.complete(StatusCodes.NoContent)
                }
              }
            }~
            patch{
              respondWithMediaType(`application/json`) {
                respondWithHeaders(RawHeader("W-Action", "profilePartialUpdate")) {
                  entity(as[List[PartialUpdate]]) {
                    request => {
                      requestContext =>
                          println("Partial Update:: "+userPublicId)
                          val privateId: Long = PublicIdGenerator.decode(userPublicId)
                          println("Partial Update:: "+privateId)
                          val user:Option[User] = getUser(privateId)
                          println("Evaluate user ")
                          if(user.isEmpty)
                            throw new ResourceNotFoundException("There is not user with this id", None)
                          println("User found...:: ")
                          val person:Option[Person] = getPerson(privateId)
                          var updatedPerson:Person = person.get
                          println("Iterating conditions... ")
                          for(condition<-request){
                            updatedPerson = partialUpdate(updatedPerson,condition)
                          }
                          println("Updating person ")
                          updateProfile(updatedPerson)
                          println("Preparing response... ")
                          val response = UserProfileDto.userProfileRequestFormat.write(toUserProfile(userPublicId,user.get,person.get)).compactPrint
                          requestContext.complete(StatusCodes.OK, HttpEntity(ContentType(MediaTypes.`application/json`, HttpCharsets.`UTF-8`), response))
                      }
                    }
                  }
                }
            }

        }



}