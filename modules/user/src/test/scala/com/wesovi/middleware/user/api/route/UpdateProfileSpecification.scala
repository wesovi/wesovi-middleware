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
class UpdateProfileSpecification extends UserProfileBaseRouteSpecification{dataBaseDriver: DatabaseDriver =>

  def beforeAll=createTables

  def afterAll=dropTables


  val userUpdateProfilePath:String = "/user"

  val updateProfileResourceDir:String ="/updateProfile/"

  val route:UserProfileRoute=new UserProfileRoute()

  val userRegistrationRoute:UserRegistrationRoute=new UserRegistrationRoute()

  val rejectionHandler = WesoviRejectionHandler.Default

  def content(json:String,mediaType:MediaType=MediaTypes.`application/json`):HttpEntity={
    HttpEntity(mediaType,json)
  }

    val conf = ConfigFactory.load()
    lazy val validApplicationToken = conf.getString("application.token")

    "The User Update Profile service" should{

      "return BadRequest when some expected field is missing" in {
        HttpRequest(method = HttpMethods.PUT, uri = userUpdateProfilePath, entity = httpEntityFromFile(updateProfileResourceDir+"requestWithNoName")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
        HttpRequest(method = HttpMethods.PUT, uri = userUpdateProfilePath, entity = httpEntityFromFile(updateProfileResourceDir+"requestWithNoGender")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }

      "return BadRequest when any of the fields has a invalid format" in {
        HttpRequest(method = HttpMethods.PUT, uri = userUpdateProfilePath, entity = httpEntityFromFile(updateProfileResourceDir+"requestWithInvalidGender")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
        HttpRequest(method = HttpMethods.PUT, uri = userUpdateProfilePath, entity = httpEntityFromFile(updateProfileResourceDir+"requestWithInvalidName")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
        HttpRequest(method = HttpMethods.PUT, uri = userUpdateProfilePath, entity = httpEntityFromFile(updateProfileResourceDir+"requestWithInvalidBirthday")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }

      "return BadRequest when the body is empty" in {
        HttpRequest(method = HttpMethods.PUT, uri = userUpdateProfilePath, entity = HttpEntity.Empty) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual BadRequest
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }


      "return UnsupportedMediaType when the content-type is not valid" in {
        HttpRequest(method = HttpMethods.PUT, uri = userUpdateProfilePath, entity = httpEntityFromFile(updateProfileResourceDir+"requestOk",MediaTypes.`application/xml`)) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual UnsupportedMediaType
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
/**
      "return UnAuthorized when the request is ok but not sending valid token" in {
        HttpRequest(method = HttpMethods.PUT, uri = userUpdateProfilePath, entity = httpEntityFromFile(updateProfileResourceDir+"requestOk")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual Unauthorized
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return Unauthorized when the request is ok but sending an invalid token" in {
        HttpRequest(method = HttpMethods.PUT, uri = userUpdateProfilePath, entity = httpEntityFromFile(updateProfileResourceDir+"requestOk"),headers=List(RawHeader("W-ApplicationToken","NoValidToken"))) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual Unauthorized
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
**/
      "return Ok when the request is ok, but there is not user with this public id" in {
        HttpRequest(method = HttpMethods.PUT, uri = userUpdateProfilePath, entity = httpEntityFromFile(updateProfileResourceDir+"requestOk")) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual NotFound
          contentType.mediaType mustEqual MediaTypes.`application/json`
          contentType.charset mustEqual HttpCharsets.`UTF-8`
        }
      }
      "return Ok when the request is ok, but there is not user with this public id" in {
        var publicId:String=""
        HttpRequest(method = HttpMethods.POST, uri = userUpdateProfilePath, entity = httpEntityFromFile(updateProfileResourceDir+"createUserOk")) ~> sealRoute(userRegistrationRoute.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
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

          HttpRequest(method = HttpMethods.GET, uri = userUpdateProfilePath+"/"+publicId) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
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


          val request:UserProfileDto = new UserProfileDto(publicId,Some("Luis"),Some("male"),None,Some("Aranjuez"),Some("Madrid"),Some("Spain"),Some("28300"))

          val updateProfileHttpEntity = HttpEntity(mediaType,UserProfileDto.userProfileRequestFormat.write(request).prettyPrint)

          HttpRequest(method = HttpMethods.PUT, uri = userUpdateProfilePath, entity = updateProfileHttpEntity) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
            status mustEqual OK
            contentType.mediaType mustEqual MediaTypes.`application/json`
            contentType.charset mustEqual HttpCharsets.`UTF-8`
            header("W-Action") mustEqual Some(RawHeader("W-Action", "updateProfile"))
            val response = responseAs[UserProfileDto]
            response.publicId must_!= null
            response.name.get mustEqual "Luis"
            response.city.get mustEqual "Aranjuez"
            response.state.get mustEqual "Madrid"
            response.country.get mustEqual "Spain"
            response.gender.get mustEqual "male"
            response.zip.get mustEqual "28300"
          }

          HttpRequest(method = HttpMethods.GET, uri = userUpdateProfilePath+"/"+publicId) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
            status mustEqual Found
            contentType.mediaType mustEqual MediaTypes.`application/json`
            contentType.charset mustEqual HttpCharsets.`UTF-8`
            header("W-Action") mustEqual Some(RawHeader("W-Action", "getProfile"))
            val response = responseAs[UserProfileDto]
            response.publicId must_!= null
            response.name.get mustEqual "Luis"
            response.city.get mustEqual "Aranjuez"
            response.state.get mustEqual "Madrid"
            response.country.get mustEqual "Spain"
            response.gender.get mustEqual "male"
            response.zip.get mustEqual "28300"
          }
        }
      }
    }

}


