package com.wesovi.middleware.common.api.domain


import com.wesovi.middleware.common.api.util.CustomJsonFormat
import spray.http.DateTime
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
 * Created by ivan on 27/1/15.
 */
object PartialUpdateType{
  val COPY ="copy"
  val MOVE ="move"
  val REPLACE ="replace"
  val ADD ="add"
  val REMOVE ="remove"
}

case class PartialUpdate(
  operation:String,
  path:String,
  from:Option[String],
  to:Option[String],
  value:Option[String]
)

object PartialUpdate extends DefaultJsonProtocol with CustomJsonFormat with SprayJsonSupport{
  implicit val dateTimeFormat = DateTime
  implicit val userProfileRequestFormat = jsonFormat5(PartialUpdate.apply)
}
