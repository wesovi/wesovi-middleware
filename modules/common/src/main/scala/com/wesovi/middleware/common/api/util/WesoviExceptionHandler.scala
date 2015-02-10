package com.wesovi.middleware.common.api.util

import java.text.ParseException

import com.wesovi.middleware.common.api.domain.ErrorResponse
import com.wesovi.middleware.common.util.WesoviException
import spray.http.StatusCodes._
import spray.http._
import spray.json.DeserializationException
import spray.routing._
import spray.util.LoggingContext

/**
 * Created by ivan on 15/1/15.
 */
trait WesoviExceptionHandler extends ExceptionHandler.PF

object WesoviExceptionHandler{


  type PF = PartialFunction[Throwable, Route]

  implicit def apply(pf: PF): ExceptionHandler =
    new ExceptionHandler {
      def isDefinedAt(error: Throwable) = pf.isDefinedAt(error)
      def apply(error: Throwable) = pf(error)
    }

  implicit def default(implicit settings: RoutingSettings, log: LoggingContext): ExceptionHandler =
    apply {

      case e: WesoviException => ctx =>{
        log.warning("Custom Exception", ctx.request)
        ctx.complete(e.httpStatus,HttpEntity(ContentType(MediaTypes.`application/json`,HttpCharsets.`UTF-8`),ErrorResponse.studentResponseFormat.write(new ErrorResponse(e.error.value,"WesoviException")).toString()))
      }
      case e:ParseException ⇒ ctx ⇒ {
        log.error(e, "Error during processing of request {}", ctx.request)
        ctx.complete(BadRequest,HttpEntity(ContentType(MediaTypes.`application/json`,HttpCharsets.`UTF-8`),ErrorInfo("Error",e.getMessage).format(true)))
      }
      case e:DeserializationException ⇒ ctx ⇒ {
        log.error(e, "Error during processing of request {}", ctx.request)
        ctx.complete(BadRequest,HttpEntity(ContentType(MediaTypes.`application/json`,HttpCharsets.`UTF-8`),ErrorInfo("Error",e.getMessage).format(true)))
      }
      case e: IllegalRequestException ⇒ ctx ⇒ {
        log.warning("Illegal request {}\n\t{}\n\tCompleting with '{}' response", ctx.request, e.getMessage, e.status)
        ctx.complete(e.status,HttpEntity(ContentType(MediaTypes.`application/json`,HttpCharsets.`UTF-8`),ErrorInfo("Error",e.getMessage).format(true)))
      }
      case e: RequestProcessingException ⇒ ctx ⇒ {
        log.warning("Request {} could not be handled normally\n\t{}\n\tCompleting with '{}' response",
          ctx.request, e.getMessage, e.status)
        ctx.complete(e.status, HttpEntity(ContentType(MediaTypes.`application/json`, HttpCharsets.`UTF-8`), ErrorInfo("Error", e.getMessage).format(true)))

      }


    }
}
