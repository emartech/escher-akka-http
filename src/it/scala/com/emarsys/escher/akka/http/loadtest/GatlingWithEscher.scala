package com.emarsys.escher.akka.http.loadtest

import java.net.URI
import java.util

import com.emarsys.escher.EscherRequest.Header
import com.emarsys.escher.{Escher, EscherRequest}
import io.gatling.core.Predef._
import io.gatling.core.body.StringBody
import io.gatling.http.Predef.http
import io.gatling.http.request.builder.HttpRequestBuilder

import scala.collection.JavaConverters._

object GatlingWithEscher {

  def createEscherRequest(method: String, url: String, headers: Seq[(String, String)], body: String): EscherRequest =
    new EscherRequest {
      private val escherHeaders : util.List[EscherRequest.Header] = new util.ArrayList[EscherRequest.Header]()

      headers.foreach(t => addHeader(t._1, t._2))

      override def addHeader(fieldName: String, fieldValue: String): Unit = escherHeaders.add(new Header(fieldName, fieldValue))

      override def getBody: String = body
      override def getRequestHeaders: util.List[EscherRequest.Header] = escherHeaders
      override def getHttpMethod: String = method
      override def getURI: URI = new URI(url)
    }

  def createEscher(credentialScope: String): Escher = {
    new Escher(credentialScope)
      .setAuthHeaderName("X-Ems-Auth")
      .setDateHeaderName("X-Ems-Date")
      .setAlgoPrefix("EMS")
      .setVendorKey("EMS")
  }

  def createHttpBuilder(requestName: String)(escher: Escher,
                                             serviceKey: String,
                                             serviceSecret: String,
                                             request: EscherRequest,
  ): HttpRequestBuilder = {

    val escherSignedRequest = escher.signRequest(
      request,
      serviceKey,
      serviceSecret,
      List("host", "X-Ems-Date").asJava
    )

    val headers = escherSignedRequest.getRequestHeaders.asScala
      .map(header => header.getFieldName -> header.getFieldValue).toMap

    http(requestName)
      .httpRequest(request.getHttpMethod, request.getURI.toString)
      .body(StringBody(request.getBody()))
      .headers(headers)
  }

}
