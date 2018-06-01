package com.emarsys.escher.akka.http.loadtest

import com.emarsys.escher.EscherRequest
import com.emarsys.escher.akka.http.config.EscherConfig
import io.gatling.core.Predef.{Simulation, atOnceUsers, _}
import io.gatling.core.config.GatlingConfiguration
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import scala.concurrent.duration._

class BigBodyLoadSpec extends Simulation {

  def httpConf(implicit configuration: GatlingConfiguration) =
    http(configuration)
      .baseURL("http://0.0.0.0:8080")

  val data = "a" * 1024 * 1024

  val request = GatlingWithEscher.createEscherRequest(
    method = "POST",
    url = s"http://0.0.0.0:8080/path",
    headers = Seq(),
    body = data
  )

  val testRunning = {
    scenario("BigBodyPost").forever() {
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
