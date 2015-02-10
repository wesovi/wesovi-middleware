package com.wesovi.middleware.common.api.util

/**
 * Created by ivan on 1/2/15.
 */
import spray.http.HttpHeaders._
import spray.http.HttpMethods._
import spray.http._
import spray.routing._



/**
 * A mixin to provide support for providing CORS headers as appropriate
 */
trait CorsSupport extends Directives{


  private val allowOriginHeader = `Access-Control-Allow-Origin`(SomeOrigins(Seq(HttpOrigin("http://localhost:4000"))))
  private val optionsCorsHeaders = List(
    `Access-Control-Allow-Headers`("Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent, W-ApplicationToken, W-AuthToken"),
    `Access-Control-Max-Age`(1728000),
    `Access-Control-Allow-Methods`(HttpMethods.POST,HttpMethods.OPTIONS),
    `Access-Control-Allow-Credentials`(true),
    `Access-Control-Allow-Origin`(SomeOrigins(Seq(HttpOrigin("http://localhost:4000"))))
  )



  import spray.http.{HttpHeaders, HttpOrigin}
  import spray.routing.Directive0

  /** Directive providing CORS header support. This should be included in any application serving
    * a REST API that's queried cross-origin (from a different host than the one serving the API).
    * See http://www.w3.org/TR/cors/ for full specification.
    * @param allowedHostnames the set of hosts that are allowed to query the API. These should
    * not include the scheme or port; they're matched only against the hostname of the Origin
    * header.
    */
  def allowHosts(allowedHostnames: Set[String]): Directive0 = mapInnerRoute { innerRoute =>
    // Conditionally responds with "allowed" CORS headers, if the request origin's host is in the
    // allowed set, or if the request doesn't have an origin.
    optionalHeaderValueByType[HttpHeaders.Origin]() { originOption =>
      // If Origin is set and the host is in our allowed set, add CORS headers and pass through.
      originOption flatMap {
        case HttpHeaders.Origin(list) => list.find {
          case HttpOrigin(_, HttpHeaders.Host(hostname, _)) => allowedHostnames.contains(hostname)
        }
      } map { goodOrigin =>
        respondWithHeaders(optionsCorsHeaders) {
            options {
              complete {
                ""
              }
            } ~
              innerRoute
          }
      } getOrElse {
        // Else, pass through without headers.
        innerRoute
      }
    }
  }



  def cors[T]: Directive0 = mapRequestContext { ctx => ctx.withRouteResponseHandling({
    // It is an option request for a resource that responds to some other method
    case Rejected(rejections) if ctx.request.method.equals(HttpMethods.OPTIONS)
      && rejections.filter(_.isInstanceOf[MethodRejection]).nonEmpty =>

      val allowedMethods: List[HttpMethod] = rejections.filter(_.isInstanceOf[MethodRejection]).map(rejection => {
        rejection.asInstanceOf[MethodRejection].supported
      })

      ctx.complete(HttpResponse().withHeaders(
        `Access-Control-Allow-Methods`(OPTIONS, allowedMethods: _*) :: allowOriginHeader ::
          optionsCorsHeaders
      ))
  }).withHttpResponseHeadersMapped { headers =>
    allowOriginHeader :: headers

  }
  }


}
