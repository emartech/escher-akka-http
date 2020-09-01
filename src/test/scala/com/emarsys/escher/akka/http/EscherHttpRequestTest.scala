package com.emarsys.escher.akka.http

import java.net.URI

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, Uri}
import com.emarsys.escher.EscherRequest

import scala.collection.JavaConverters._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EscherHttpRequestTest extends AnyFlatSpec with Matchers {

  private val httpRequest = HttpRequest(
    method = HttpMethods.DELETE,
    uri = Uri("http://suite.ett.local"),
    headers = List(RawHeader("host", "trunk.suite.ett.local")),
    entity = HttpEntity("Hello ሴ")
  )

  private val escherRequest = new EscherHttpRequest(httpRequest, "Hello ሴ")

  "Spray Escher request" should "get body with utf8 characters" in {

    escherRequest.getBody shouldBe "Hello ሴ"
  }

  it should "get http method" in {
    escherRequest.getHttpMethod shouldBe "DELETE"
  }

  it should "get uri" in {
    escherRequest.getURI shouldBe new URI("http://suite.ett.local")
  }

  it should "get http request" in {
    escherRequest.getHttpRequest shouldBe httpRequest
  }

  it should "return false when has header called for a non existing key" in {
    escherRequest.hasHeader("date") shouldBe false
  }

  it should "return true when has header called for an existing key" in {
    escherRequest.hasHeader("host") shouldBe true
  }

  it should "add other header with existing key" in {
    val eRequest = new EscherHttpRequest(httpRequest, "")
    eRequest.addHeader("host", "bar")
    eRequest.getRequestHeaders() shouldBe List(new EscherRequest.Header("host", "trunk.suite.ett.local"), new EscherRequest.Header("Host", "bar")).asJava
  }

  it should "return true when has header called after adding it" in {
    val eRequest = new EscherHttpRequest(httpRequest, "")
    eRequest.hasHeader("foo") shouldBe false
    eRequest.addHeader("foo", "bar")
    eRequest.hasHeader("foo") shouldBe true
  }

}
