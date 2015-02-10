package com.wesovi.middleware.common.api.util

import spray.http.StatusCodes._
import spray.http._
import spray.routing.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}
import spray.routing._
import spray.routing.directives.RouteDirectives._

/**
 * Created by ivan on 15/1/15.
 */
trait WesoviRejectionHandler extends RejectionHandler.PF{


}

object WesoviRejectionHandler{ctx:RequestContext=>

  type PF = PartialFunction[List[Rejection], Route]

  implicit def apply(pf: PF): RejectionHandler =

    new RejectionHandler {
      def isDefinedAt(rejections: List[Rejection]) = pf.isDefinedAt(rejections)
      def apply(rejections: List[Rejection]) = pf(rejections)
    }

  implicit def completeWithFormat(httpError:ClientError,msg:String,contentType:ContentType=ContentType(MediaTypes.`application/json`,HttpCharsets.`UTF-8`))={


    complete(httpError,HttpEntity(contentType,msg))
  }

  implicit val Default = apply {

    case Nil ⇒ completeWithFormat(NotFound, "The requested resource could not be found.")

    case AuthenticationFailedRejection(cause, challengeHeaders) :: _ ⇒
      val rejectionMessage = cause match {
        case CredentialsMissing  ⇒ "The resource requires authentication, which was not supplied with the request"
        case CredentialsRejected ⇒ "The supplied authentication is invalid"
      }
    { ctx ⇒ ctx.complete(Unauthorized, challengeHeaders, rejectionMessage) }

    case AuthorizationFailedRejection :: _ ⇒
      completeWithFormat(Forbidden, "The supplied authentication is not authorized to access this resource")

    case CorruptRequestEncodingRejection(msg) :: _ ⇒
      completeWithFormat(BadRequest, "The requests encoding is corrupt:\n" + msg)

    case MalformedFormFieldRejection(name, msg, _) :: _ ⇒
      completeWithFormat(BadRequest, "The form field '" + name + "' was malformed:\n" + msg)

    case MalformedHeaderRejection(headerName, msg, _) :: _ ⇒
      completeWithFormat(BadRequest, s"The value of HTTP header '$headerName' was malformed:\n" + msg)

    case MalformedQueryParamRejection(name, msg, _) :: _ ⇒
      complete(BadRequest, "The query parameter '" + name + "' was malformed:\n" + msg)

    case MalformedRequestContentRejection(msg, _) :: _ ⇒
      completeWithFormat(BadRequest, "The request content was malformed:\n" + msg)

    case rejections @ (MethodRejection(_) :: _) ⇒
      val methods = rejections.collect { case MethodRejection(method) ⇒ method }
      completeWithFormat(MethodNotAllowed, "HTTP method not allowed, supported methods: " + methods.mkString(", "))

    case rejections @ (SchemeRejection(_) :: _) ⇒
      val schemes = rejections.collect { case SchemeRejection(scheme) ⇒ scheme }
      completeWithFormat(BadRequest, "Uri scheme not allowed, supported schemes: " + schemes.mkString(", "))

    case MissingCookieRejection(cookieName) :: _ ⇒
      completeWithFormat(BadRequest, "Request is missing required cookie '" + cookieName + '\'')

    case ApplicationTokenRejection(_) :: _ ⇒

      completeWithFormat(Unauthorized, "Application token no valid.")

    case MissingFormFieldRejection(fieldName) :: _ ⇒
      completeWithFormat(BadRequest, "Request is missing required form field '" + fieldName + '\'')

    case MissingHeaderRejection(headerName) :: _ ⇒
      completeWithFormat(BadRequest, "Request is missing required HTTP header '" + headerName + '\'')

    case MissingQueryParamRejection(paramName) :: _ ⇒
      completeWithFormat(NotFound, "Request is missing required query parameter '" + paramName + '\'')

    case RequestEntityExpectedRejection :: _ ⇒
      completeWithFormat(BadRequest, "Request entity expected but not supplied")

    case TooManyRangesRejection(_) :: _ ⇒
      completeWithFormat(RequestedRangeNotSatisfiable, "Request contains too many ranges.")

    case UnsatisfiableRangeRejection(unsatisfiableRanges, actualEntityLength) :: _ ⇒
      completeWithFormat(RequestedRangeNotSatisfiable,
        unsatisfiableRanges.mkString("None of the following requested Ranges were satisfiable:\n", "\n", ""))

    case rejections @ (UnacceptedResponseContentTypeRejection(_) :: _) ⇒
      val supported = rejections.flatMap {
        case UnacceptedResponseContentTypeRejection(supported) ⇒ supported
        case _ ⇒ Nil
      }
      completeWithFormat(NotAcceptable, "Resource representation is only available with these Content-Types:\n" + supported.map(_.value).mkString("\n"))

    case rejections @ (UnacceptedResponseEncodingRejection(_) :: _) ⇒
      val supported = rejections.collect { case UnacceptedResponseEncodingRejection(supported) ⇒ supported }
      completeWithFormat(NotAcceptable, "Resource representation is only available with these Content-Encodings:\n" + supported.map(_.value).mkString("\n"))

    case rejections @ (UnsupportedRequestContentTypeRejection(_) :: _) ⇒
      val supported = rejections.collect { case UnsupportedRequestContentTypeRejection(supported) ⇒ supported }
      completeWithFormat(UnsupportedMediaType, "There was a problem with the requests Content-Type:\n" + supported.mkString(" or "))

    case rejections @ (UnsupportedRequestEncodingRejection(_) :: _) ⇒
      val supported = rejections.collect { case UnsupportedRequestEncodingRejection(supported) ⇒ supported }
      completeWithFormat(BadRequest, "The requests Content-Encoding must be one the following:\n" + supported.map(_.value).mkString("\n"))

    case ValidationRejection(msg, _) :: _ ⇒
      completeWithFormat(BadRequest, msg)
  }



  /**
   * Filters out all TransformationRejections from the given sequence and applies them (in order) to the
   * remaining rejections.
   */
  def applyTransformations(rejections: List[Rejection]): List[Rejection] = {
    val (transformations, rest) = rejections.partition(_.isInstanceOf[TransformationRejection])
    (rest.distinct /: transformations.asInstanceOf[Seq[TransformationRejection]]) {
      case (remaining, transformation) ⇒ transformation.transform(remaining)
    }
  }

}


