package com.wesovi.middleware.common.application.database

import scala.slick.driver.JdbcProfile

trait DatabaseDriver {
  val profile: JdbcProfile
}

