package com.wesovi.middleware.common.application.database

import com.typesafe.config.ConfigFactory

import scala.slick.driver.JdbcProfile



case class DatabaseConnection(override val profile: JdbcProfile) extends DatabaseDriver{
  import profile.simple._

  def dbObject(): Database = {
    val env = scala.util.Properties.envOrElse("runMode", "dev")
    val config = ConfigFactory.load(env)
    val url = config.getString("db.url")
    val username = config.getString("db.username")
    val password = config.getString("db.password")
    val driver = config.getString("db.driver")
    Database.forURL(url, username, password, null, driver)
  }

}
