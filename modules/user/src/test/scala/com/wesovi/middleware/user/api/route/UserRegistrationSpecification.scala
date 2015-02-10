package com.wesovi.middleware.user.api.route

import com.typesafe.config.ConfigFactory
import com.wesovi.middleware.common.api.util.{WesoviExceptionHandler, WesoviRejectionHandler}
import com.wesovi.middleware.common.application.database.DatabaseDriver
import com.wesovi.middleware.user.api.domain.RegistrationResponseDto
import spray.http.HttpHeaders.RawHeader
import spray.http.StatusCodes._
import spray.http._
/**
 * Created by ivan on 15/1/15.
 */
class UserRegistrationSpecification extends UserProfileBaseRouteSpecification{dataBaseDriver: DatabaseDriver =>

  def beforeAll=createTables

  def afterAll=dropTables


  val userRegistrationPath:String = "/user"

  val registrationResourceDir:String ="/registration/"

  val route:UserRegistrationRoute=new UserRegistrationRoute()

  val rejectionHandler = WesoviRejectionHandler.Default

  def content(json:String,mediaType:MediaType=MediaTypes.`application/json`):HttpEntity={
    HttpEntity(mediaType,json)
  }

    val conf = ConfigFactory.load()
    lazy val validApplicationToken = conf.getString("application.token")



    "The UserRegistration service" should{

      "return BadRequest when some expected field is missing" in {
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = httpEntityFromFile(registrationResourceDir+"requestWithNoEmail")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = httpEntityFromFile(registrationResourceDir+"requestWithNoPassword")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }

      "return BadRequest when any of the fields has a invalid format" in {
        println("validApplicationToken "+validApplicationToken)
        val customEntity:HttpEntity = httpEntityFromFile(registrationResourceDir+"requestWithInvalidEmail")
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = customEntity) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = httpEntityFromFile(registrationResourceDir+"requestWithInvalidPassword")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }

      "return BadRequest when the fbRequest is empty" in {
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = HttpEntity.Empty) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }

      "return Conflict when there's already a user with the same email" in {
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = httpEntityFromFile(registrationResourceDir+"requestOk2")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual Created
          HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = httpEntityFromFile(registrationResourceDir+"requestOk2")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
            status mustEqual Conflict
            contentType.mediaType mustEqual MediaTypes.`application/json`
            contentType.charset mustEqual HttpCharsets.`UTF-8`
          }
        }
      }
      "return BadRequest when any of the fields has a invalid format" in {
        println("validApplicationToken "+validApplicationToken)
        val customEntity:HttpEntity = httpEntityFromFile(registrationResourceDir+"requestWithInvalidEmail")
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = customEntity) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return UnsupportedMediaType when the content-type is not valid" in {
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = httpEntityFromFile(registrationResourceDir+"requestOk3",MediaTypes.`application/xml`)) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual UnsupportedMediaType
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      /**
      "return Unauthorized when the fbRequest is ok and there is no yet user with this email in the system, but not sending valid token" in {
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = httpEntityFromFile(registrationResourceDir+"requestOk")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual Unauthorized
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return Unauthorized when the fbRequest is ok and there is no yet user with this email in the system, but sending an invalid token" in {
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = httpEntityFromFile(registrationResourceDir+"requestOk"),headers=List(RawHeader("W-ApplicationToken","NoValidToken"))) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual Unauthorized
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
  **/
      "return Created when the fbRequest is ok and there is not a user with this email in the system, and sending a valid token" in {
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = httpEntityFromFile(registrationResourceDir+"requestOk")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {

          status mustEqual Created
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
          header("W-Action") mustEqual Some(RawHeader("W-Action","registration"))
          val response = responseAs[RegistrationResponseDto]
          response.email mustEqual "user001@mail.com"
          response.publicId must_!= null
          response.birthday mustEqual None
          response.gender mustEqual None
          response.name mustEqual None
        }
      }
    }

}


