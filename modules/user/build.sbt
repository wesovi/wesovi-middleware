import Dependencies._

Build.Settings.project

name := "user"

libraryDependencies ++= Seq(
  akka.actor,
  akka.cluster,
  akka.contrib,
  akka.slf4j,
  spray.routing,
  spray.json,
 // scalaz.core,
  spray.testkit % "test",
  akka.testkit % "test",
  scala_reflect,
  scalatest% "test",
  specs2 ,
  //akka.persistence_cassandra,


  slick.slick,
  mysql.connector
)
