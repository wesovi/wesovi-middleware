package com.wesovi.middleware.common.application.database

import scala.slick.driver.JdbcProfile
import scala.slick.driver.H2Driver
import scala.slick.driver.MySQLDriver
import scala.slick.driver.PostgresDriver

object SlickDBDriver {
  val TEST = "test"
  val DEV = "dev"
  val PROD = "prod"
  def getDriver: JdbcProfile = {
    scala.util.Properties.envOrElse("runMode", "dev") match {
    //println("Get Drive r" +System.getProperty("runMode"))
    //System.getProperty("runMode") match{
      case TEST => H2Driver
      case DEV => MySQLDriver
      case PROD => PostgresDriver
      case _ => MySQLDriver
    }
  }
}
