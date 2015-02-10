package com.wesovi.middleware.user.api.domain

import java.util.Date

import com.wesovi.middleware.common.api.util.{CustomJsonFormat, EmailValidation}
import com.wesovi.middleware.common.util.BadRequestException
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
 * Created by ivan on 10/1/15.
 */

case class FacebookRegistrationDto(facebookId:String,email:String,name:String,gender:Option[String],birthday:Option[Date]){

  def validate()={
    assume(email!=null,"email must not be null")
    if(email.isEmpty || !EmailValidation.isValid(email))
      throw new BadRequestException("Invalid email format",None)
    if(name.isEmpty || name.length<3)
      throw new BadRequestException("Invalid name format",None)
    if(facebookId.isEmpty || facebookId.length<3)
      throw new BadRequestException("Invalid facebookId format",None)
    if(gender.isEmpty || !Seq("male","female").contains(gender.get))
      throw new BadRequestException("Invalid gender format",None)
  }
}

object FacebookRegistrationDto extends DefaultJsonProtocol with CustomJsonFormat{
  implicit val dateTimeFormat = DateJsonFormat
  implicit val facebookRegistrationRequestFormat = jsonFormat5(FacebookRegistrationDto.apply)
}

case class BasicRegistrationDto(email:String,password:String) {

  def validate()= {
    assume(email != null, "email must not be null")
    if (email.isEmpty || !EmailValidation.isValid(email))
      throw new BadRequestException("Invalid email format", None)
    if (password.isEmpty || password.length < 3)
      throw new BadRequestException("Invalid password format", None)
  }
}

object BasicRegistrationDto extends DefaultJsonProtocol with CustomJsonFormat{
  implicit val registrationRequestFormat = jsonFormat2(BasicRegistrationDto.apply)
}

case class RegistrationResponseDto(publicId:String,email:String,name:Option[String],gender:Option[String],birthday:Option[Date]){
  assume(publicId!=null,"user was not registered correctly.")
  assume(EmailValidation.isValid(email),"email is valid")
}

object RegistrationResponseDto extends DefaultJsonProtocol with CustomJsonFormat with SprayJsonSupport{
  implicit val dateJsonFormat = DateJsonFormat
  implicit val registrationResponseFormat = jsonFormat5(RegistrationResponseDto.apply)
}