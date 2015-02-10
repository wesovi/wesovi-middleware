package com.wesovi.middleware.common.util

/**
 * Created by ivan on 24/1/15.
 */
object PublicIdGenerator {

  var USER_PREFIX:String="USR"

  val random = new scala.util.Random

  def encode(privateId:Long,prefix:String,maxLength:Int=20): String ={
    val numberOfRandomCharacters = maxLength-prefix.length-String.valueOf(privateId).length
    val randomCharacters = randomAlphanumericString(numberOfRandomCharacters)
    val output = prefix concat randomCharacters concat "WSV"
    val randomNumber =  output.lastIndexOf("WSV")
    output concat String.valueOf(privateId) concat String.valueOf(randomNumber)
  }

  def decode(publicId:String):Long={
    val numberOfRandomCharacters = publicId.lastIndexOf("WSV")
    var currentPublicId = publicId.substring(numberOfRandomCharacters+3)
    val privateIdStr = currentPublicId.substring(0,currentPublicId.lastIndexOf(String.valueOf(numberOfRandomCharacters)))
    privateIdStr.toLong
  }

  // Generate a random string of length n from the given alphabet
  def randomString(alphabet: String)(n: Int): String =
    Stream.continually(random.nextInt(alphabet.size)).map(alphabet).take(n).mkString

  // Generate a random alphabnumeric string of length n
  def randomAlphanumericString(n: Int) =
    randomString("ABCDEFGHIJKLMNOPQRSTUVXYZ0123456789")(n)

}
