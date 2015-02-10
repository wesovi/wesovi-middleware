package com.wesovi.middleware.user.api.domain

import com.wesovi.middleware.common.api.util.CustomJsonFormat
import com.wesovi.middleware.common.util.BadRequestException
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Created by ivan on 27/1/15.
  */

case class ChangePasswordDto(
   oldPassword:Option[String],
   password:Option[String]) {

      def validate()={
        if(oldPassword.isEmpty || oldPassword.get.length<3){
          throw new BadRequestException("Invalid password format",None)
        }
        if(password.isEmpty || password.get.length<3){
          throw new BadRequestException("Invalid gender format",None)
        }
      }



    }


object ChangePasswordDto extends DefaultJsonProtocol with CustomJsonFormat with SprayJsonSupport{
   implicit val userProfileRequestFormat = jsonFormat2(ChangePasswordDto.apply)
 }
