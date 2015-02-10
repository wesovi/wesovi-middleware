import Dependencies._

Build.Settings.project

name := "administrator"

libraryDependencies ++= Seq(
  akka.actor,
  akka.cluster,
  akka.contrib,
  spray.routing,
  spray.json,
 // scalaz.core,
  scala_reflect,
  akka.persistence_cassandra,
  akka.testkit % "test",
  spray.testkit % "test"
)

