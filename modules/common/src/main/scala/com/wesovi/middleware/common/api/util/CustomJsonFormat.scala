package com.wesovi.middleware.common.api.util

import java.text.SimpleDateFormat
import java.util.Date

import com.sun.xml.internal.ws.encoding.soap.DeserializationException
import spray.http.DateTime
import spray.json._

/**
 * Created by ivan on 13/1/15.
 */
trait CustomJsonFormat {

  implicit object DateJsonFormat extends RootJsonFormat[Option[Date]] {

    val sdf = new SimpleDateFormat("dd/MM/yyyy")

    override def write(obj: Option[Date]) = {
      if(!obj.isEmpty)
        JsString(sdf.format(obj.get))
      else
        JsNull
    }

    override def read(json: JsValue) : Option[Date] = json match {
      case JsString(s) => Some(sdf.parse(s))
      case JsNull =>None
      case _ => throw new DeserializationException("Error info you want here ...")
    }
  }

  implicit object DateTimeJsonFormat extends RootJsonFormat[Option[DateTime]] {

    override def write(obj: Option[DateTime]) = JsString(obj.get.toIsoDateString)

    override def read(json: JsValue) : Option[DateTime] = json match {
      case JsString(s) => DateTime.fromIsoDateTimeString(s)
      case _ => throw new DeserializationException("Error info you want here ...")
    }
  }

}
