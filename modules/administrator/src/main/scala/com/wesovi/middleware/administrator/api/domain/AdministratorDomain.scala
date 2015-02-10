package com.wesovi.middleware.administrator.api.domain

import com.wesovi.middleware.common.api.util.CustomJsonFormat
import com.wesovi.middleware.common.util.BadRequestException
import spray.http.DateTime
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
 * Created by ivan on 2/2/15.
 */
case class AdminDto(
  username:String,
  password:String){

  def validate()={
    if(username.isEmpty){
      throw new BadRequestException("Invalid username",None)
    }
    if(password.isEmpty){
      throw new BadRequestException("Invalid password",None)
    }
  }
}

object AdminDto extends DefaultJsonProtocol with SprayJsonSupport{
  implicit val userProfileRequestFormat = jsonFormat2(AdminDto.apply)
}

case class AuthTokenDto(
  value:String,
  expirationDate:Option[DateTime]
)

object AuthTokenDto extends DefaultJsonProtocol with CustomJsonFormat with SprayJsonSupport{

  implicit val dateTimeFormat = DateTimeJsonFormat

  implicit val authTokenFormat = jsonFormat2(AuthTokenDto.apply)
}


