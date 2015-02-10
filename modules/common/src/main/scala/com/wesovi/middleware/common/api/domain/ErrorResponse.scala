package com.wesovi.middleware.common.api.domain

/**
 * Created by ivan on 11/1/15.
 */
import spray.json.DefaultJsonProtocol

case class ErrorResponse(
                          code:String,
                          description:String
                          )

object ErrorResponse extends DefaultJsonProtocol{

  implicit val studentResponseFormat = jsonFormat2(ErrorResponse.apply)

}

