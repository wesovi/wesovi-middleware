package com.wesovi.middleware.user.api.domain

import com.wesovi.middleware.common.api.util.{CustomJsonFormat, EmailValidation}
import com.wesovi.middleware.common.util.BadRequestException
import spray.json.DefaultJsonProtocol

/**
  * Created by ivan on 28/1/15.
  */
case class UserAuthenticationDto(
   email:String,
   password:String
 ) {
    def validate()={
      if(email.isEmpty || !EmailValidation.isValid(email)){
        throw new BadRequestException("Invalid password format",None)
      }
      if(password.isEmpty || password.length<3){
        throw new BadRequestException("Invalid gender format",None)
      }
    }
  }

object UserAuthenticationDto extends DefaultJsonProtocol with CustomJsonFormat{
   implicit val userAuthenticationFormat = jsonFormat2(UserAuthenticationDto.apply)
 }
