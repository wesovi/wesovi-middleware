package com.wesovi.middleware.common.util

import com.typesafe.config.ConfigFactory

trait ConfigHolder {

  var PROP_HOSTNAME:String="hostname"

  var PROP_PORT:String="port"

  var PROP_ORIGIN_DOMAIN ="origin.domain"

  var PROP_ADMIN_USERNAME ="admin.username"

  var PROP_ADMIN_PASSWORD ="admin.password"

  val config = ConfigFactory.load()

  def getHostname():String={
    config.getString(PROP_HOSTNAME)
  }

  def getPort():Int={
    config.getInt(PROP_PORT)
  }

  def getOriginDomain():String={
    config.getString(PROP_ORIGIN_DOMAIN)
  }

  def getAdminUsername():String={
    config.getString(PROP_ADMIN_USERNAME)
  }
  def getAdminPassword():String={
    config.getString(PROP_ADMIN_PASSWORD)
  }

}
