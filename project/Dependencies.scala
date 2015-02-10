import sbt._

object Dependencies {

  object akka {
    val version = "2.3.8"
    // Core Akka
    val slf4j                 = "com.typesafe.akka"      %%  "akka-slf4j"                   % version
    val actor                 = "com.typesafe.akka"      %% "akka-actor"                    % version
    val cluster               = "com.typesafe.akka"      %% "akka-cluster"                  % version
    val contrib               = "com.typesafe.akka"      %% "akka-contrib"                  % version intransitive()
    val persistence           = "com.typesafe.akka"      %% "akka-persistence-experimental" % version intransitive()
    val persistence_cassandra = "com.github.krasserm"    %% "akka-persistence-cassandra"    % "0.3.6" intransitive()

    object streams {
      val version = "1.0-M2"

      val core      = "com.typesafe.akka" %% "akka-stream-experimental"         % version
      val http      = "com.typesafe.akka" %% "akka-http-experimental"           % version
      val http_core = "com.typesafe.akka" %% "akka-http-core-experimental"      % version

      // FIXME: currently this is an empty dependency - as soon as this situation changes, we need to ensure manually added files under `akka.stream.testkit.*` are deleted!
      val testkit   = "com.typesafe.akka" %% "akka-stream-testkit-experimental" % version
    }

    val leveldb               = "org.iq80.leveldb"        % "leveldb"                       % "0.7"

    val testkit               = "com.typesafe.akka"      %% "akka-testkit"                  % version
  }

  object spray {
    val version = "1.3.2"
    val sprayJsonVersion = "1.3.1"

    val httpx   = "io.spray" %% "spray-httpx"              % version
    val can     = "io.spray" %% "spray-can"                % version
    val routing = "io.spray" %% "spray-routing-shapeless2" % version
    val client  = "io.spray" %% "spray-client"             % version
    val testkit = "io.spray" %% "spray-testkit"            % version
    val json    = "io.spray" %% "spray-json"               % sprayJsonVersion
  }


  object mysql{
    val version ="latest.version"
    val connector = "mysql" % "mysql-connector-java" % version
  }

  object slick{
    val slickVersion = "2.1.0"
    val slick = "com.typesafe.slick"  %%    "slick" % slickVersion
  }

  object kafka {
    val version = "0.8.2-beta"
    val kafka   = "org.apache.kafka" %% "kafka"  % version exclude("org.slf4j", "slf4j-simple") exclude("org.slf4j","slf4j-log4j12")
  }

  object scalaz {
    val core = "org.scalaz" %% "scalaz-core" % "7.1.0"
  }

  object json4s {
    val native  = "org.json4s" %% "json4s-native" % "3.2.11"
    val jackson = "org.json4s" %% "json4s-jackson" % "3.2.11"
  }

  object scalanlp {
    val version = "0.10"

    val breeze  = "org.scalanlp" %% "breeze"         % version
    val natives = "org.scalanlp" %% "breeze-natives" % version
    val nak     = "org.scalanlp" %% "nak"            % "1.3" exclude("org.scalanlp", "breeze_2.11") exclude("org.scalanlp", "breeze-natives_2.11")
  }

  val scodec_bits      = "org.typelevel"    %% "scodec-bits"  % "1.0.4"
  val parboiled        = "org.parboiled"    %% "parboiled"    % "2.0.1"

  // Scala reflect
  val scala_reflect    = "org.scala-lang"   % "scala-reflect" % "2.11.5"

  // Apple push notifications
  val apns             = "com.notnoop.apns"  % "apns"         % "0.1.6"
  val slf4j_simple     = "org.slf4j"         % "slf4j-simple" % "1.6.1"

  // Datastax Cassandra Client
  val cassandra_driver = "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.1" exclude("io.netty", "netty")

  // Testing
  val scalatest        = "org.scalatest"    %% "scalatest"    % "2.2.1"
  val scalacheck       = "org.scalacheck"   %% "scalacheck"   % "1.12.1"
  val specs2           = "org.specs2"       %%    "specs2"    % "2.3.12" % "test"

}
