package com.wesovi.middleware.user.api.domain

import java.util.Date

import com.wesovi.middleware.common.api.util.CustomJsonFormat
import com.wesovi.middleware.common.util.BadRequestException
import spray.http.DateTime
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
 * Created by ivan on 11/1/15.
 */
case class UserProfileDto(
  publicId:String,
  name:Option[String],
  gender:Option[String],
  birthday:Option[Date],
  city:Option[String],
  state:Option[String],
  country:Option[String],
  zip:Option[String]) {

  def validate()={
    if(name.isEmpty || name.get.length<3)
      throw new BadRequestException("Invalid name format",None)
    if(gender.isEmpty || !Seq("male","female").contains(gender.get))
      throw new BadRequestException("Invalid gender format",None)
  }
}

object UserProfileDto extends DefaultJsonProtocol with CustomJsonFormat with SprayJsonSupport{
  implicit val dateTimeFormat = DateTime
  implicit val userProfileRequestFormat = jsonFormat8(UserProfileDto.apply)
}

