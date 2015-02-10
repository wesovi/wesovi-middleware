package com.wesovi.middleware.user.api.route

import java.text.SimpleDateFormat

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
class FbUserRegistrationSpecification extends UserProfileBaseRouteSpecification{dataBaseDriver: DatabaseDriver =>

  val sdf = new SimpleDateFormat("dd/MM/yyyy")

  def beforeAll=createTables

  def afterAll=dropTables

  val facebookUserRegistrationPath:String = "/user/registration/facebook"
  
  val fbRegistrationResourceDir:String ="/fbRegistration/"

  var route:UserRegistrationRoute=new UserRegistrationRoute()

  val rejectionHandler = WesoviRejectionHandler.Default

  def content(json:String,mediaType:MediaType=MediaTypes.`application/json`):HttpEntity={
    HttpEntity(mediaType,json)
  }

    val conf = ConfigFactory.load()
    lazy val validApplicationToken = conf.getString("application.token")




    "The FacebookRegistration service" should {

      "return BadRequest when birthday field is not well formatted" in {
        HttpRequest(method = HttpMethods.POST, uri = facebookUserRegistrationPath, entity = httpEntityFromFile(fbRegistrationResourceDir+"fbRequestWithInvalidDateFormat")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }

      "return BadRequest when some expected field is missing" in {
        HttpRequest(method = HttpMethods.POST, uri = facebookUserRegistrationPath, entity = httpEntityFromFile(fbRegistrationResourceDir+"fbRequestWithNoFacebookId")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
        HttpRequest(method = HttpMethods.POST, uri = facebookUserRegistrationPath, entity = httpEntityFromFile(fbRegistrationResourceDir+"fbRequestWithNoEmail")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return BadRequest when any of the fields has a invalid format" in {
        HttpRequest(method = HttpMethods.POST, uri = facebookUserRegistrationPath, entity = httpEntityFromFile(fbRegistrationResourceDir+"fbRequestWithNoValidEmail")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
        HttpRequest(method = HttpMethods.POST, uri = facebookUserRegistrationPath, entity = httpEntityFromFile(fbRegistrationResourceDir+"fbRequestWithNoValidGender")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
        HttpRequest(method = HttpMethods.POST, uri = facebookUserRegistrationPath, entity = httpEntityFromFile(fbRegistrationResourceDir+"fbRequestWithNoValidName")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return BadRequest when the fbRequest is empty" in {
        HttpRequest(method = HttpMethods.POST, uri = facebookUserRegistrationPath, entity = HttpEntity.Empty) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return Conflict when there's already a user with the same email" in {
        HttpRequest(method = HttpMethods.POST, uri = facebookUserRegistrationPath, entity = httpEntityFromFile(fbRegistrationResourceDir+"fbRequestOk2")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual Created
          HttpRequest(method = HttpMethods.POST, uri = facebookUserRegistrationPath, entity = httpEntityFromFile(fbRegistrationResourceDir+"fbRequestOk2")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
            status mustEqual Conflict
            contentType.mediaType mustEqual MediaTypes.`application/json`
            contentType.charset mustEqual HttpCharsets.`UTF-8`
          }
        }
      }
      "return BadRequest when any of the fields has a invalid format" in {
        HttpRequest(method = HttpMethods.POST, uri = facebookUserRegistrationPath, entity = httpEntityFromFile(fbRegistrationResourceDir+"fbRequestWithNoValidEmail")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return UnsupportedMediaType when the content-type is not valid" in {
        HttpRequest(method = HttpMethods.POST, uri = facebookUserRegistrationPath, entity = httpEntityFromFile(fbRegistrationResourceDir+"fbRequestOk3",MediaTypes.`application/xml`)) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual UnsupportedMediaType
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }

      }
/**
      "return Unauthorized when the fbRequest is ok and there is no yet user with this email in the system, but not sending valid token" in {
        HttpRequest(method = HttpMethods.POST, uri = facebookUserRegistrationPath, entity = httpEntityFromFile(fbRegistrationResourceDir+"fbRequestOk")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual Unauthorized
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return Unauthorized when the fbRequest is ok and there is no yet user with this email in the system, but sending an invalid token" in {
        HttpRequest(method = HttpMethods.POST, uri = facebookUserRegistrationPath, entity = httpEntityFromFile(fbRegistrationResourceDir+"fbRequestOk"),headers=List(RawHeader("W-ApplicationToken","NoValidToken"))) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual Unauthorized
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
  **/
      "return Created when the fbRequest is ok and there is not a user with this email in the system, and sending a valid token" in {
        HttpRequest(method = HttpMethods.POST, uri = facebookUserRegistrationPath, entity = httpEntityFromFile(fbRegistrationResourceDir+"fbRequestOk")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual Created
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
          header("W-Action") mustEqual Some(RawHeader("W-Action","facebookRegistration"))
          val response = responseAs[RegistrationResponseDto]
          response.publicId must_!= null
          response.email mustEqual "ivan@mail.com"
          sdf.format(response.birthday.get) mustEqual "12/11/2005"
          response.gender.get mustEqual "male"
          response.name.get mustEqual "Juan"
        }

      }
      println("Terminated...")



    }
}


