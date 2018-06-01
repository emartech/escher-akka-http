package com.emarsys.escher.akka.http.loadtest

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import com.emarsys.escher.akka.http.EscherDirectives
import com.emarsys.escher.akka.http.config.EscherConfig

class TestApplication extends EscherDirectives {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  override val escherConfig: EscherConfig =
    new EscherConfig(com.typesafe.config.ConfigFactory.load().getConfig("escher"))

  implicit val logger = system.log

  lazy val start = {
    val route =
      ((get | post) & path("path"))(escherAuthenticate(escherConfig.services)(_ => complete("OK")))

    Http().bindAndHandle(route, "0.0.0.0", 8080)
  }

  def stop = {
    start.flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
