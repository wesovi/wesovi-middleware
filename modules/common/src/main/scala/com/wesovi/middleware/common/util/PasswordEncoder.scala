package com.wesovi.middleware.common.util

/**
 * Created by ivan on 27/1/15.
 */
object PasswordEncoder {

  def encode(password:String):String= {
    java.security.MessageDigest.getInstance("MD5").digest(password.getBytes()).map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
  }

}
