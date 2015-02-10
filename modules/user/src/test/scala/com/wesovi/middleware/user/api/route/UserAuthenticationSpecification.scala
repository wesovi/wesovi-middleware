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
class UserAuthenticationSpecification extends UserProfileBaseRouteSpecification{dataBaseDriver: DatabaseDriver =>

  def beforeAll=createTables

  def afterAll=dropTables


  val userAuthenticationPath:String = "/user/auth"

  val userRegistrationPath:String = "/user"

  val userAuthenticationResourceDir:String ="/authentication/"

  val route:UserAuthenticationRoute=new UserAuthenticationRoute()

  val userRegistrationRoute:UserRegistrationRoute=new UserRegistrationRoute()

  val rejectionHandler = WesoviRejectionHandler.Default

  def content(json:String,mediaType:MediaType=MediaTypes.`application/json`):HttpEntity={
    HttpEntity(mediaType,json)
  }

    val conf = ConfigFactory.load()
    lazy val validApplicationToken = conf.getString("application.token")
    lazy val invalidApplicationToken = "NoValid"
    val validPublicId ="USR102020202020239WSV10718"
    val invalidPublicId ="USR102029WSV1071"

    "The Change Password service" should {


      "return BadRequest when some expected field is missing" in {
        HttpRequest(method = HttpMethods.POST, uri = userAuthenticationPath, entity = httpEntityFromFile(userAuthenticationResourceDir + "missingEmail")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return BadRequest when some field is not correct" in {
        HttpRequest(method = HttpMethods.POST, uri = userAuthenticationPath, entity = httpEntityFromFile(userAuthenticationResourceDir + "invalidPassword")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
        HttpRequest(method = HttpMethods.POST, uri = userAuthenticationPath, entity = httpEntityFromFile(userAuthenticationResourceDir + "invalidEmail")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      /**
      "return UnAuthorized when the request is ok but not sending valid token" in {
        HttpRequest(method = HttpMethods.POST, uri = userAuthenticationPath, entity = httpEntityFromFile(userAuthenticationResourceDir + "validRequest")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual Unauthorized
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return Unauthorized when the request is ok but sending an invalid token" in {
        HttpRequest(method = HttpMethods.POST, uri = userAuthenticationPath, entity = httpEntityFromFile(userAuthenticationResourceDir + "validRequest")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual Unauthorized
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
        **/
      "return NotFound when there is not a user with this credentials" in {
        HttpRequest(method = HttpMethods.POST, uri = userAuthenticationPath.replace("$user",validPublicId), entity = httpEntityFromFile(userAuthenticationResourceDir+"validRequest")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual NotFound
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }

      "return NotFound when the password is incorrect" in {
        var publicId:String=""
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = httpEntityFromFile(userAuthenticationResourceDir + "createUserOk")) ~> sealRoute(userRegistrationRoute.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual Created
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
          header("W-Action") mustEqual Some(RawHeader("W-Action", "registration"))
          val response = responseAs[RegistrationResponseDto]
          response.publicId must_!= null
          publicId = response.publicId
        }
        HttpRequest(method = HttpMethods.POST, uri = userAuthenticationPath.replace("$user",publicId), entity = httpEntityFromFile(userAuthenticationResourceDir+"validRequest")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual NotFound
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }

      "return NoContent when the the password is changed correctly" in {
        var publicId:String=""
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = httpEntityFromFile(userAuthenticationResourceDir + "createUserOk2")) ~> sealRoute(userRegistrationRoute.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual Created
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
          header("W-Action") mustEqual Some(RawHeader("W-Action", "registration"))
          val response = responseAs[RegistrationResponseDto]
          response.publicId must_!= null
          publicId = response.publicId
        }
        HttpRequest(method = HttpMethods.POST, uri = userAuthenticationPath.replace("$user",publicId), entity = httpEntityFromFile(userAuthenticationResourceDir+"validRequest2")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual NoContent
          header("W-Action") mustEqual Some(RawHeader("W-Action", "authenticateUser"))
        }
      }

    }
}


