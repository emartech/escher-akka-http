package com.emarsys.escher.akka.http.loadtest


import com.emarsys.escher.EscherRequest
import com.emarsys.escher.akka.http.config.EscherConfig
import io.gatling.core.Predef.{Simulation, atOnceUsers, _}
import io.gatling.core.config.GatlingConfiguration
import io.gatling.http.{HeaderNames, HeaderValues}
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import concurrent.duration._

class SimpleLoadSpec extends Simulation {

  def httpConf(implicit configuration: GatlingConfiguration) =
    http(configuration)
      .baseURL("http://0.0.0.0:8080")

  val request = GatlingWithEscher.createEscherRequest(
    method = "GET",
    url = s"http://0.0.0.0:8080/path",
    headers = Seq(),
    body = ""
  )

  val testRunning = {
    scenario("SimpleGet").forever() {
      exec(
        createRequest("TestRequest", request)
      )
    }

  }

  lazy val escherConfig: EscherConfig = new EscherConfig(com.typesafe.config.ConfigFactory.load().getConfig("escher"))

  lazy val escher = GatlingWithEscher.createEscher(escherConfig.credentialScope)

  def createRequest(requestName: String, request: EscherRequest): HttpRequestBuilder = GatlingWithEscher.createHttpBuilder(requestName)(escher, escherConfig.key("service1"), escherConfig.secret("service1"), request)

  val app = new TestApplication

  before(app.start)

  setUp(
    testRunning.inject(
      atOnceUsers(5),
      rampUsers(145) over (45.seconds)
    )
  ).maxDuration(2.minutes).protocols(httpConf)

  after(
    app.stop
  )

}
