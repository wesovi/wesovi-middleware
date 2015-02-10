package com.wesovi.middleware.user.api.route

import com.typesafe.config.ConfigFactory
import com.wesovi.middleware.common.api.util.{WesoviExceptionHandler, WesoviRejectionHandler}
import com.wesovi.middleware.common.application.database.DatabaseDriver
import com.wesovi.middleware.user.api.domain.{UserProfileDto, RegistrationResponseDto}
import spray.http.HttpHeaders.RawHeader
import spray.http.StatusCodes._
import spray.http._


/**
 * Created by ivan on 25/1/15.
 */
class CancelAccountSpecification extends UserProfileBaseRouteSpecification{dataBaseDriver: DatabaseDriver =>

  def beforeAll=createTables

  def afterAll=dropTables

  val publicId:String="USR102020202020239WSV10718"

  val cancelProfileResourceDir:String ="/cancelAccount/"

  val cancelUserAccountPath:String = "/user"

  val getProfilePath:String = "/user"

  val registerUserPath:String = "/user"

  val userUpdateProfilePath:String = "/user"

  val route:UserProfileRoute=new UserProfileRoute()

  val userRegistrationRoute:UserRegistrationRoute=new UserRegistrationRoute()

  val rejectionHandler = WesoviRejectionHandler.Default

  val conf = ConfigFactory.load()
  lazy val validApplicationToken = conf.getString("application.token")

  "The User Cancel Account service" should{


  /**
    "return UnAuthorized when not sending valid token" in {
      HttpRequest(method = HttpMethods.DELETE, uri = cancelUserAccountPath+"/"+publicId) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
        status mustEqual Unauthorized
      }
    }
    "return Unauthorized when sending an invalid token" in {
      HttpRequest(method = HttpMethods.DELETE, uri = cancelUserAccountPath+"/"+publicId,headers=List(RawHeader("W-ApplicationToken","NoValidToken"))) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
        status mustEqual Unauthorized
      }
    }
  **/
    "return NotFound when there is not user with this public id" in {
      HttpRequest(method = HttpMethods.DELETE, uri = cancelUserAccountPath+"/"+publicId) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
        status mustEqual NotFound
      }
    }

    "return NotFound when there is not user with this public id" in {
      HttpRequest(method = HttpMethods.DELETE, uri = cancelUserAccountPath+"/"+publicId) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
        status mustEqual NotFound
      }
    }

    "cancel account successfuly" in{
      HttpRequest(method = HttpMethods.POST, uri = registerUserPath, entity = httpEntityFromFile(cancelProfileResourceDir+"createUserOk")) ~> sealRoute(userRegistrationRoute.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
        status mustEqual Created
        contentType.mediaType mustEqual MediaTypes.`application/json`
        contentType.charset mustEqual HttpCharsets.`UTF-8`
        header("W-Action") mustEqual Some(RawHeader("W-Action","registration"))
        val response = responseAs[RegistrationResponseDto]
        response.email mustEqual "userToBeDeleted@mail.com"
        response.publicId must_!= null
        response.birthday mustEqual None
        response.gender mustEqual None
        response.name mustEqual None

        val request:UserProfileDto = new UserProfileDto(response.publicId,Some("Luis"),Some("male"),None,Some("Aranjuez"),Some("Madrid"),Some("Spain"),Some("28300"))

        val updateProfileHttpEntity = HttpEntity(mediaType,UserProfileDto.userProfileRequestFormat.write(request).prettyPrint)

        HttpRequest(method = HttpMethods.PUT, uri = userUpdateProfilePath, entity = updateProfileHttpEntity, headers = List(RawHeader("W-ApplicationToken", validApplicationToken))) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
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

        HttpRequest(method = HttpMethods.GET, uri = getProfilePath+"/"+response.publicId,headers = List(RawHeader("W-ApplicationToken", validApplicationToken))) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler) ~> check {
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

        HttpRequest(method = HttpMethods.DELETE, uri = cancelUserAccountPath+"/"+response.publicId) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual NoContent
        }

        HttpRequest(method = HttpMethods.GET, uri = getProfilePath+"/"+response.publicId) ~> sealRoute(route.route)(WesoviExceptionHandler.default, rejectionHandler)  ~> check {
          status mustEqual NotFound
        }
      }
    }

  }

}
