package com.wesovi.middleware.user.api.route

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.ActorRefFactory
import akka.pattern.ask
import akka.util.Timeout
import com.wesovi.middleware.common.api.ApiRoute
import com.wesovi.middleware.common.api.util.ApplicationTokenValidator
import com.wesovi.middleware.common.util.{DuplicatedResourceException, PublicIdGenerator, PasswordEncoder}
import com.wesovi.middleware.user.ActorLocator
import com.wesovi.middleware.user.api.domain.{FacebookRegistrationDto, BasicRegistrationDto, RegistrationResponseDto}
import com.wesovi.middleware.user.application.database.{User, Person}
import com.wesovi.middleware.user.application.database.PersonRepositoryMessage.{UpdatePersonMessage, NewPersonMessage}
import com.wesovi.middleware.user.application.database.UserRepositoryMessage.{FindUserByEmail, NewUserMessage}
import spray.http.HttpHeaders.RawHeader
import spray.http._
import spray.routing.Directive.pimpApply
import spray.routing._
import spray.util.LoggingContext

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

object UserRegistrationRoute {

}

class UserRegistrationRoute (implicit ec: ExecutionContext, actorRefFactory:ActorRefFactory,log: LoggingContext) extends ApiRoute with ApplicationTokenValidator{

  import com.wesovi.middleware.common.api.ApiRoute.ApiRouteProtocol._

  val sdf = new SimpleDateFormat("dd/MM/yyyy")

  implicit val timeout = Timeout(10 seconds)

  val personService = ActorLocator.personRepository(actorRefFactory)
  val userService = ActorLocator.userRepository(actorRefFactory)

  def createUser(email:String,password:Option[String],name:Option[String],gender:Option[String],birthday:Option[Date],ctx:RequestContext):RegistrationResponseDto= {
    var encodedPassword:Option[String] =None
    if(password.isDefined){
      encodedPassword = Some(PasswordEncoder.encode(password.get))
    }else{
      encodedPassword = None
    }
    val userCreationFuture: Future[Long] = (userService ? NewUserMessage(email, encodedPassword)).mapTo[Long]
    val personCreationFuture: Future[Long] = (personService ? NewPersonMessage(None, name, gender, birthday)).mapTo[Long]

    val objectsCreation:Future[(Long,Long)] = for {
      userId:Long <- userCreationFuture
      personId:Long <- personCreationFuture
    } yield (userId,personId)
    val ids = Await.result[(Long,Long)](objectsCreation,timeout.duration)
    val updatePersonFuture = personService ? UpdatePersonMessage(Person(Some(ids._2),Some(ids._1),name,gender,birthday,None,None,None,None))
    updatePersonFuture.onSuccess{
      case _ => println("Person was updated.")
    }
    new RegistrationResponseDto(PublicIdGenerator.encode(ids._1,"USR",35), email, name, gender, birthday)
  }

  def basicRegistration(request:BasicRegistrationDto,ctx:RequestContext):RegistrationResponseDto={
    log.debug("User registration "+request.toString)
    request.validate
    val existingUser =  Await.result(userService ? FindUserByEmail(request.email),timeout.duration).asInstanceOf[Option[User]]
    if (existingUser!=None) {
      throw new DuplicatedResourceException("Email already in use", None)
    }else{
      createUser(request.email,Some(request.password),None,None,None,ctx)
    }
  }

  def facebookRegistration(request:FacebookRegistrationDto,ctx:RequestContext):RegistrationResponseDto={
    log.debug("Facebook registration-> "+request.toString)
    request.validate
    val existingUser =  Await.result(userService ? FindUserByEmail(request.email),timeout.duration).asInstanceOf[Option[User]]
    if (existingUser.isDefined) {
      throw new DuplicatedResourceException("Email already in use", None)
    }else{
      createUser(request.email,None,Some(request.name),request.gender,request.birthday,ctx)
    }
  }

  val route: Route =
      //cors{
      //validateApplicationToken { applicationToken =>
        path("user") {
          post {
            respondWithMediaType(MediaTypes.`application/json`) {
              respondWithHeaders(RawHeader("W-Action", "registration")) {
                entity(as[BasicRegistrationDto]) {
                  request => {
                    requestContext =>
                      val response = basicRegistration(request, requestContext)
                      val responseStr = RegistrationResponseDto.registrationResponseFormat.write(response).prettyPrint
                      requestContext.complete(StatusCodes.Created, HttpEntity(ContentType(MediaTypes.`application/json`, HttpCharsets.`UTF-8`), responseStr))
                  }
                }
              }
            }
          }
        } ~ path("user" / "registration" / "facebook") {
          post {
            respondWithMediaType(MediaTypes.`application/json`) {
              respondWithHeaders(RawHeader("W-Action", "facebookRegistration")) {
                entity(as[FacebookRegistrationDto]) {
                  request => {
                    requestContext =>
                      val response = facebookRegistration(request, requestContext)
                      val responseStr = RegistrationResponseDto.registrationResponseFormat.write(response).prettyPrint
                      requestContext.complete(StatusCodes.Created, HttpEntity(ContentType(MediaTypes.`application/json`, HttpCharsets.`UTF-8`), responseStr))
                  }
                }
              }
            }
          }
        }
    //  }
  //  }

}
