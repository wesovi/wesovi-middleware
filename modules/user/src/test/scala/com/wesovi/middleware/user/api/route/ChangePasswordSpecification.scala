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
class ChangePasswordSpecification extends UserProfileBaseRouteSpecification{dataBaseDriver: DatabaseDriver =>

  def beforeAll=createTables

  def afterAll=dropTables


  val changePasswordPath:String = "/user/$userId/password/action/change"

  val userRegistrationPath:String = "/user"

  val changePasswordResourceDir:String ="/changePassword/"

  val route:UserCredentialsRoute=new UserCredentialsRoute()

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
        HttpRequest(method = HttpMethods.PUT, uri = changePasswordPath.replace("$user", validPublicId), entity = httpEntityFromFile(changePasswordResourceDir + "missingOldPassword")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return BadRequest when some field is not correct" in {
        HttpRequest(method = HttpMethods.PUT, uri = changePasswordPath.replace("$user", validPublicId), entity = httpEntityFromFile(changePasswordResourceDir + "invalidPassword")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      /**
      "return UnAuthorized when the request is ok but not sending valid token" in {
        HttpRequest(method = HttpMethods.PUT, uri = changePasswordPath.replace("$user", validPublicId), entity = httpEntityFromFile(changePasswordResourceDir + "validRequest")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual Unauthorized
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return Unauthorized when the request is ok but sending an invalid token" in {
        HttpRequest(method = HttpMethods.PUT, uri = changePasswordPath.replace("$user", validPublicId), entity = httpEntityFromFile(changePasswordResourceDir + "validRequest")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual Unauthorized
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      **/
      /**
      "return NotFound when sending an invalid user private id" in {
        HttpRequest(method = HttpMethods.PUT, uri = changePasswordPath.replace("$user", invalidPublicId), entity = httpEntityFromFile(changePasswordResourceDir + "validRequest")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual NotFound
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
    **/

      "return NotFound when there is not a user with this privateId" in {
        HttpRequest(method = HttpMethods.PUT, uri = changePasswordPath.replace("$user",validPublicId), entity = httpEntityFromFile(changePasswordResourceDir+"validRequest")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual NotFound
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }

      "return BadRequest when the oldPassword is incorrect" in {
        var publicId:String=""
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = httpEntityFromFile(changePasswordResourceDir + "createUserOk")) ~> sealRoute(userRegistrationRoute.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual Created
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
          header("W-Action") mustEqual Some(RawHeader("W-Action", "registration"))
          val response = responseAs[RegistrationResponseDto]
          response.publicId must_!= null
          publicId = response.publicId
        }
        HttpRequest(method = HttpMethods.PUT, uri = changePasswordPath.replace("$user",publicId), entity = httpEntityFromFile(changePasswordResourceDir+"validRequest")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }


      }

      "return NoContent when the the password is changed correctly" in {
        var publicId:String=""
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = httpEntityFromFile(changePasswordResourceDir + "createUserOk2")) ~> sealRoute(userRegistrationRoute.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual Created
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
          header("W-Action") mustEqual Some(RawHeader("W-Action", "registration"))
          val response = responseAs[RegistrationResponseDto]
          response.publicId must_!= null
          publicId = response.publicId
        }
        HttpRequest(method = HttpMethods.PUT, uri = changePasswordPath.replace("$user",publicId), entity = httpEntityFromFile(changePasswordResourceDir+"validRequest2")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual NoContent
          header("W-Action") mustEqual Some(RawHeader("W-Action", "changePassword"))
        }


      }

    }
}


