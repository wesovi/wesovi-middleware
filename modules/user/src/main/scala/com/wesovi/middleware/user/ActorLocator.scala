package com.wesovi.middleware.user

import akka.actor.{ActorRefFactory, Props}

import com.wesovi.middleware.user.application.database.{UserRepository, PersonRepository}
import com.wesovi.middleware.common.application.database.{SlickDBDriver, DatabaseDriver}

import scala.slick.driver.JdbcProfile

/**
 * Created by ivan on 11/1/15.
 */
object ActorLocator extends DatabaseDriver{this: DatabaseDriver =>

  def personRepository(implicit ctx: ActorRefFactory) = ctx.actorOf(Props(new PersonRepository(profile)))
  def userRepository(implicit ctx: ActorRefFactory) = ctx.actorOf(Props(new UserRepository(profile)))


  override val profile: JdbcProfile = SlickDBDriver.getDriver


}
