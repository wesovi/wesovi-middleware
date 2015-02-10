package com.wesovi.middleware.user.api.route

import com.typesafe.config.ConfigFactory
import com.wesovi.middleware.common.api.util.{WesoviExceptionHandler, WesoviRejectionHandler}
import com.wesovi.middleware.common.application.database.DatabaseDriver
import com.wesovi.middleware.user.api.domain.{UserProfileDto, RegistrationResponseDto}
import spray.http.HttpHeaders.RawHeader
import spray.http.StatusCodes._
import spray.http._

/**
 * Created by ivan on 15/1/15.
 */
class PartialUpdateProfileSpecification extends UserProfileBaseRouteSpecification{dataBaseDriver: DatabaseDriver =>

  def beforeAll=createTables

  def afterAll=dropTables


  val userPartialUpdateProfilePath:String = "/user"

  val partialUpdateProfileResourceDir:String ="/partialUpdateProfile/"

  val route:UserProfileRoute=new UserProfileRoute()

  val userRegistrationRoute:UserRegistrationRoute=new UserRegistrationRoute()

  val rejectionHandler = WesoviRejectionHandler.Default

  def content(json:String,mediaType:MediaType=MediaTypes.`application/json`):HttpEntity={
    HttpEntity(mediaType,json)
  }

    val conf = ConfigFactory.load()
    lazy val validApplicationToken = conf.getString("application.token")
    val validPublicId ="USR102020202020239WSV10718"

    "The User Update Profile service" should{
      /**
      "return BadRequest when some expected field is missing" in {

      }

      "return BadRequest when any of the fields has a invalid format" in {

      }
      **/


      /**
      "return BadRequest when the body is empty" in {
        HttpRequest(method = HttpMethods.PATCH, uri = userPartialUpdateProfilePath+"/"+validPublicId, entity = HttpEntity.Empty) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      **/

      /**
      "return UnsupportedMediaType when the content-type is not valid" in {
        HttpRequest(method = HttpMethods.PATCH, uri = userPartialUpdateProfilePath+"/"+validPublicId, entity = httpEntityFromFile(partialUpdateProfileResourceDir+"requestOK",MediaTypes.`application/xml`)) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual UnsupportedMediaType
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
       **/
      
      /**
      "return UnAuthorized when the request is ok but not sending valid token" in {
        HttpRequest(method = HttpMethods.PATCH, uri = userPartialUpdateProfilePath+"/"+validPublicId, entity = httpEntityFromFile(partialUpdateProfileResourceDir+"requestOk")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual Unauthorized
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return Unauthorized when the request is ok but sending an invalid token" in {
        HttpRequest(method = HttpMethods.PATCH, uri = userPartialUpdateProfilePath+"/"+validPublicId, entity = httpEntityFromFile(partialUpdateProfileResourceDir+"requestOk"),headers=List(RawHeader("W-ApplicationToken","NoValidToken"))) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual Unauthorized
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
**/
      "return Ok when the request is ok, but there is not user with this public id" in {
        HttpRequest(method = HttpMethods.PATCH, uri = userPartialUpdateProfilePath+"/"+validPublicId, entity = httpEntityFromFile(partialUpdateProfileResourceDir+"requestOk")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual NotFound
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "Update partially multiple fields" in {
        var publicId:String=""
        HttpRequest(method = HttpMethods.POST, uri = userPartialUpdateProfilePath, entity = httpEntityFromFile(partialUpdateProfileResourceDir+"createUserOk")) ~> sealRoute(userRegistrationRoute.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual Created
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
          header("W-Action") mustEqual Some(RawHeader("W-Action","registration"))
          val response = responseAs[RegistrationResponseDto]
          response.email mustEqual "user991@mail.com"
          response.publicId must_!= null
          response.birthday mustEqual None
          response.gender mustEqual None
          response.name mustEqual None


          publicId = response.publicId

          HttpRequest(method = HttpMethods.GET, uri = userPartialUpdateProfilePath+"/"+publicId) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
            status mustEqual Found
            contentType.mediaType mustEqual MediaTypes.`application/json`
            contentType.charset mustEqual HttpCharsets.`UTF-8`
            header("W-Action") mustEqual Some(RawHeader("W-Action", "getProfile"))
            val response = responseAs[UserProfileDto]
            response.publicId must_!= null
            response.name mustEqual None
            response.city mustEqual None
            response.state mustEqual None
            response.country mustEqual None
            response.gender mustEqual None
            response.zip mustEqual None
          }


          HttpRequest(method = HttpMethods.PATCH, uri = userPartialUpdateProfilePath+"/"+publicId, entity = httpEntityFromFile(partialUpdateProfileResourceDir+"requestOk"), headers = List(RawHeader("W-ApplicationToken", validApplicationToken))) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
            status mustEqual OK
            contentType.mediaType mustEqual MediaTypes.`application/json`
            contentType.charset mustEqual HttpCharsets.`UTF-8`
            header("W-Action") mustEqual Some(RawHeader("W-Action", "profilePartialUpdate"))
            val response = responseAs[UserProfileDto]
            response.publicId must_!= null
            response.name.get mustEqual "PepitoGrillo"
            response.city mustEqual None
            response.state mustEqual None
            response.country mustEqual None
            response.gender mustEqual None
            response.zip mustEqual None
          }

          HttpRequest(method = HttpMethods.GET, uri = userPartialUpdateProfilePath+"/"+publicId,headers = List(RawHeader("W-ApplicationToken", validApplicationToken))) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
            status mustEqual Found
            contentType.mediaType mustEqual MediaTypes.`application/json`
            contentType.charset mustEqual HttpCharsets.`UTF-8`
            header("W-Action") mustEqual Some(RawHeader("W-Action", "getProfile"))
            val response = responseAs[UserProfileDto]
            response.publicId must_!= null
            response.name.get mustEqual "PepitoGrillo"
            response.city mustEqual None
            response.state mustEqual None
            response.country mustEqual None
            response.gender mustEqual None
            response.zip mustEqual None
          }
        }
      }
    }

}


