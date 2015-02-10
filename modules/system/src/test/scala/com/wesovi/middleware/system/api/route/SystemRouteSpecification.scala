package com.wesovi.middleware.system.api.route

import org.scalatest.prop.Configuration
import org.specs2.mutable.{Before, Specification}
import spray.http.StatusCodes._
import spray.testkit.Specs2RouteTest
/**
 * Created by ivan on 15/1/15.
 */


class SystemRouteSpecification extends Specification
with Specs2RouteTest
with Configuration with Before{

  // makes test execution sequential and prevents conflicts that may occur when the data is
  // changed simultaneously in the database
  args(sequential = true)

  val statusPath:String = "/system/status"

  implicit def actorRefFactory = system

  val systemService = new SystemRoute

  def before=


  "The EndPoint " should {
    "return ok to a Get request to the ping" in {
      Get(statusPath) ~> systemService.route ~> check {
        status === Accepted
        responseAs[String].toLowerCase() === "ok"
      }
    }
  }
}
