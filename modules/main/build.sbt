import Dependencies._

Build.Settings.project

name := "main"

libraryDependencies ++= Seq(
  akka.actor,
  akka.slf4j,
  akka.cluster,
  akka.contrib,
  spray.httpx,
  spray.can,
  spray.routing,
  //scalaz.core,
  scala_reflect,
  //akka.persistence_cassandra,
  akka.testkit % "test",
  Dependencies.slf4j_simple,
  spray.testkit % "test"
)