import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

organization := "com.wesovi"

name := "middleware"

version := "1.0"

startYear := Some(2015)

lazy val common = project.in(file("modules/common"))

lazy val administrator = project.in(file("modules/administrator")).dependsOn(common)

lazy val system = project.in(file("modules/system")).dependsOn(common)

lazy val user = project.in(file("modules/user")).dependsOn(common)

lazy val main = project.in(file("modules/main")).dependsOn(administrator,system,user)

lazy val root = (project in file(".")).aggregate(main,administrator,system,user)



fork in Test := false

fork in IntegrationTest := false

testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "junitxml")

parallelExecution in Test := false

publishLocal := {}

publish := {}

maintainer := "Ivan Corrales Solera <ivan.corrales.solera@gmail.com>"

dockerExposedPorts in Docker := Seq(1600)

dockerEntrypoint in Docker := Seq("sh", "-c", "CLUSTER_IP=`/sbin/ifconfig eth0 | grep 'inet addr:' | cut -d: -f2 | awk '{ print $1 }'` bin/clustering $*")

dockerRepository := Some("mhamrah")

enablePlugins(JavaAppPackaging)

