package com.wesovi.middleware.common.util

object WesoviError extends Enumeration{
  
  type operation = Value

  val MissingRequiredAttribute = Value("ERROOOO1","ERROOOO1")

  val DatabaseCommunicationError = Value("ERROOOO2","ERROOOO1")

  val UnexpectedException = Value("ERROOOO3","ERROOOO1")

  val DuplicatedElement = Value("ERROOOO3","ERROOOO1")

  val InvalidFormat = Value("ERROOOO3","ERROOOO1")

  val InvalidToken = Value("ERROOOO3","ERROOOO1")

  val InvalidCredentials = Value("ERROOOO3","ERROOOO1")

  val ResourceNotFound = Value("ERROOOO3","ERROOOO1")

  val UnAuthorized = Value("ERROOOO3","ERROOOO1")

  val BadRequest = Value("ERROOOO3","ERROOOO1")



  class WesoviErrorVal(name:String, val value : String) extends Val(nextId,name)

  protected final def Value(name: String, value : String): WesoviErrorVal = new WesoviErrorVal(name,value)


}