package com.wesovi.middleware.main

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import com.wesovi.middleware.common.util.ConfigHolder


trait Core {
  val env = scala.util.Properties.envOrElse("wlRunMode", "dev")
  val configuration = ConfigFactory.load(env)
  implicit def system: ActorSystem
}

trait BootedCore extends Core {


  implicit lazy val system = ActorSystem("wl-system",configuration)
  sys.addShutdownHook(system.shutdown())

}

trait CoreActors extends ConfigHolder {
  this: Core =>

}


