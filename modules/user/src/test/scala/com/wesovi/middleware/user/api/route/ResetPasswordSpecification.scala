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
class ResetPasswordSpecification extends UserProfileBaseRouteSpecification{dataBaseDriver: DatabaseDriver =>

  def beforeAll=createTables

  def afterAll=dropTables


  val resetPasswordPath:String = "/user/$userId/password/action/reset"

  val userRegistrationPath:String = "/user"

  val resetPasswordResourceDir:String ="/resetPassword/"

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

    "The Reset Password service" should {
/**
      "return UnAuthorized when the request is ok but not sending valid token" in {
        HttpRequest(method = HttpMethods.PUT, uri = resetPasswordPath.replace("$user", validPublicId)) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual Unauthorized
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return Unauthorized when the request is ok but sending an invalid token" in {
        HttpRequest(method = HttpMethods.PUT, uri = resetPasswordPath.replace("$user", validPublicId)) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual Unauthorized
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
  **/
      /**
      "return NotFound when sending an invalid user private id" in {
        HttpRequest(method = HttpMethods.PUT, uri = resetPasswordPath.replace("$user", invalidPublicId), ) headers = List(RawHeader("W-ApplicationToken", validApplicationToken))) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual NotFound
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
    **/

      "return NotFound when there is not a user with this privateId" in {
        HttpRequest(method = HttpMethods.PUT, uri = resetPasswordPath.replace("$user",validPublicId)) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual NotFound
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }

      "return NoContent when the the password is resetted correctly" in {
        var publicId:String=""
        HttpRequest(method = HttpMethods.POST, uri = userRegistrationPath, entity = httpEntityFromFile(resetPasswordResourceDir + "createUserOk2")) ~> sealRoute(userRegistrationRoute.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
          status mustEqual Created
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
          header("W-Action") mustEqual Some(RawHeader("W-Action", "registration"))
          val response = responseAs[RegistrationResponseDto]
          response.publicId must_!= null
          publicId = response.publicId
        }
        HttpRequest(method = HttpMethods.PUT, uri = resetPasswordPath.replace("$user",publicId)) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual NoContent
          header("W-Action") mustEqual Some(RawHeader("W-Action", "resetPassword"))
        }


      }

    }
}


