package com.wesovi.middleware.common.util

import spray.http.StatusCodes

class WesoviException(ex:Throwable) extends RuntimeException(ex) {

  var error: WesoviError.WesoviErrorVal = _

  var httpStatus:Int=_

  var message:String=_

  def this(httpStatus:Int,error: WesoviError.WesoviErrorVal, message: String, throwable: Option[Throwable]) = {
    this(throwable.getOrElse(null))
    this.error = error
    this.httpStatus = httpStatus
    this.message=message
  }

}
object BadRequestException

class BadRequestException(message: String, throwable: Option[Throwable]) extends WesoviException(StatusCodes.BadRequest.intValue,WesoviError.BadRequest,message,throwable)

class DuplicatedResourceException(message: String, throwable: Option[Throwable]) extends WesoviException(StatusCodes.Conflict.intValue,WesoviError.DuplicatedElement,message,throwable)

class ResourceNotFoundException(message: String, throwable: Option[Throwable]) extends WesoviException(StatusCodes.NotFound.intValue,WesoviError.ResourceNotFound,message,throwable)

class UnAuthorizedUserException(message: String, throwable: Option[Throwable]) extends WesoviException(StatusCodes.Unauthorized.intValue,WesoviError.UnAuthorized,message,throwable)

class UnexpectedException(message: String, throwable: Option[Throwable]) extends WesoviException(StatusCodes.InternalServerError.intValue,WesoviError.UnexpectedException,message,throwable)
