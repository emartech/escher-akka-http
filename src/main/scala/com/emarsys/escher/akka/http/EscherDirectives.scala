package com.emarsys.escher.akka.http

import akka.event.LoggingAdapter
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.{HttpHeader, HttpRequest}
import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.emarsys.escher.EscherException
import spray.json._

import scala.collection.JavaConversions._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Try, Failure, Success}

trait EscherDirectives extends RequestBuilding with EscherAuthenticator {

  def signRequest(serviceName: String)(implicit ec: ExecutionContext, mat: Materializer): (HttpRequest) => Future[HttpRequest] = { r =>
    signRequestWithHeaders(Nil)(serviceName)(ec,mat)(r)
  }

  def signRequestWithHeaders(headers : List[HttpHeader])(serviceName:String)(implicit ec: ExecutionContext, mat: Materializer): (HttpRequest) => Future[HttpRequest] = { r =>

    val escher = setupEscher(createEscherForSigning(serviceName))
    val defaultSignedHeaders = List("host", escherConfig.dateHeaderName)

    for {
      body <- Unmarshal(r.entity).to[String]
    } yield {
      val escherRequest: EscherHttpRequest = new EscherHttpRequest(r, body)
      escher.signRequest(
        escherRequest,
        escherConfig.key(serviceName),
        escherConfig.secret(serviceName),
        defaultSignedHeaders.union(headers.map(_.name))
      )
      escherRequest.getHttpRequest.addHeaders(headers)
    }
  }

  def escherAuthenticate[T](trustedServiceNames: List[String])(inner: (String) => Route)
                           (implicit ec: ExecutionContext, mat: Materializer, logger: LoggingAdapter): Route = {
    extract(_.request).map {
      case r: HttpRequest => authenticate(trustedServiceNames, r)
      case msg            => Future.failed(new EscherException("Failed to parse HTTP request"))
    }.apply(onComplete(_) {
      case Success(value) => inner(value)
      case Failure(ex) =>
        logger.debug(ex.getMessage)
        reject(
          AuthenticationFailedRejection(
            AuthenticationFailedRejection.CredentialsRejected, HttpChallenge("Basic", "Escher")))
    })
  }

  def parseBody[T](body: String)(inner: T => Route)(implicit format: RootJsonFormat[T]): Route = {
    Try(body.parseJson.convertTo[T]) match {
      case Success(parsed) => inner(parsed)
      case Failure(x)      => reject(MalformedRequestContentRejection(x.getMessage, Option(x.getCause)))
    }
  }
}
