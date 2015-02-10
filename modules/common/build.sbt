import Dependencies._

Build.Settings.project

name := "common"

libraryDependencies ++= Seq(
  akka.actor,
  akka.cluster,
  akka.contrib,
  spray.routing,
  spray.json,
  // scalaz.core,
  scala_reflect,
  slick.slick,
  mysql.connector,
  scalatest,
  specs2,
  //akka.persistence_cassandra,
  akka.testkit % "test",
  spray.testkit % "test"
)
