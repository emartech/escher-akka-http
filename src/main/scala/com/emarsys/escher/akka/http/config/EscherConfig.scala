package com.emarsys.escher.akka.http.config
import com.typesafe.config.Config

import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.{Success, Try}

class EscherConfig(config: Config) {
  val authHeaderName: String = config.getString("auth-header-name")
  val dateHeaderName: String = config.getString("date-header-name")
  val algoPrefix: String = config.getString("algo-prefix")
  val vendorKey: String = config.getString("vendor-key")
  val hostName: String = config.getString("hostname")
  val port: Int = config.getInt("port")
  val allowNonHttpsForwardedProto: Boolean = Try(config.getBoolean("allow-non-https-forwarded-proto")).getOrElse(false)

  val credentialScope: String = config.getString("credential-scope")
  val headersToSign: Seq[String] = Try{config.getStringList("headers-to-sign").asScala.toList}.getOrElse(List("host", "X-Ems-Date"))

  val trustedServices: List[Config] = config.getConfigList("trusted-services").asScala.toList

  private def findTrustedService(service: String) = trustedServices.find(_.getString("name") == service)
  val services = trustedServices.map(_.getString("name")).toList

  def key(service: String): String = findTrustedService(service).map(_.getString("key")).getOrElse("")

  def secret(service: String): String = findTrustedService(service).map(config => readFromFileOrConf(config, "secret")).getOrElse("")

  def credentialScope(service: String): String = findTrustedService(service).map(_.getString("credential-scope")).getOrElse(credentialScope)

  private def readFromFileOrConf(config: Config, path: String): String = {
    Try(config.getString(s"$path-file")) match {
      case Success(fileName) => readFromFile(fileName)
      case _                 => config.getString(path)
    }
  }

  private def readFromFile(fileName: String): String = {
    val source = Source.fromFile(fileName)
    try {
      source.mkString
    } finally {
      source.close()
    }
  }

}
