package com.wesovi.middleware.user.api.route

import akka.util.Timeout
import com.wesovi.middleware.user.ActorLocator
import com.wesovi.middleware.user.application.database.{PersonRepositoryMessage, UserRepositoryMessage}

import org.scalatest.prop.Configuration
import org.specs2.mutable.Specification
import org.specs2.specification.{Fragments, Step}
import spray.http.{HttpEntity, MediaType, MediaTypes}
import spray.routing.HttpService
import spray.testkit.Specs2RouteTest

import scala.concurrent.duration.DurationInt
import scala.io.Source

/**
 * Created by ivan on 10/2/15.
 */
abstract class UserProfileBaseRouteSpecification  extends Specification
with Specs2RouteTest
with Configuration
with HttpService {

  private val requestBaseDir:String="/request-body"
  private val jsonFileSuffix:String="json"

  implicit def actorRefFactory = system
  implicit val timeout = Timeout(10 microseconds)

  // makes test execution sequential and prevents conflicts that may occur when the data is
  // changed simultaneously in the database
  args(sequential = true)

  //def after = system.shutdown()

  override def map(fragments: =>Fragments) =
    Step(beforeAll) ^ fragments ^ Step(afterAll)

  def httpEntityFromFile(requestFileName:String,mediaType:MediaType=MediaTypes.`application/json`):HttpEntity={
    val filePath = requestBaseDir+requestFileName+"."+jsonFileSuffix
    println("URI: "+getClass.getResource(filePath))
    val stringContent = Source.fromURL(getClass.getResource(filePath)).mkString
    HttpEntity(mediaType,stringContent)
  }


  protected def beforeAll()
  protected def afterAll()
  def createTables {
     var future:Unit =ActorLocator.userRepository ! UserRepositoryMessage.CreateTable
     future =ActorLocator.personRepository ! PersonRepositoryMessage.CreateTable
    Thread.sleep(1000)
  }

  def dropTables {
    Thread.sleep(1000)
    var future:Unit =ActorLocator.userRepository ! UserRepositoryMessage.DropTable
    future =ActorLocator.personRepository ! PersonRepositoryMessage.DropTable
  }

}
