package com.emarsys.escher.akka.http.config

import java.io.{File, FileNotFoundException, PrintWriter}
import java.nio.file.Files

import org.scalatest.{Matchers, WordSpec}

class EscherConfigTest extends WordSpec with Matchers {

  private val tempFilePath = {
    val path = Files.createTempFile("escher-test-", ".secret").toAbsolutePath.toString
    val pw = new PrintWriter(new File(path))
    pw.write("secret from file")
    pw.close()
    path
  }

  private val configString =
    s"""escher {
       |  credential-scope = "eu/service/ems_request"
       |  auth-header-name = "X-Ems-Auth"
       |  date-header-name = "X-Ems-Date"
       |  algo-prefix = "EMS"
       |  vendor-key = "Escher"
       |  hostname = "ip-192-168-56-1.ec2.internal"
       |  port = "9000"
       |
       |  trusted-services = [
       |    {
       |      name = "simple-service1"
       |      key = "service1-key"
       |      secret = "service1-secret-key-0123"
       |      credential-scope = "eu/service/ems_request"
       |    },
       |    {
       |      name = "from-file-service2"
       |      key = "service2-key"
       |      secret = "service2-secret-key-0123"
       |      secret-file = "$tempFilePath"
       |      credential-scope = "eu/service/ems_request"
       |    },
       |    {
       |      name = "wrong-file-service3"
       |      key = "service3-key"
       |      secret = "service3-secret-key-0123"
       |      secret-file = "RANDOM_WRONG_PATH"
       |      credential-scope = "eu/service/ems_request"
       |    }
       |  ]
       |}
       |""".stripMargin

  private val config = com.typesafe.config.ConfigFactory.parseString(configString).getConfig("escher")

  private val escherConfig: EscherConfig = new EscherConfig(config)

  "EscherConfig #secret" should {
    "read secret from config" in {
      escherConfig.secret("simple-service1") shouldEqual "service1-secret-key-0123"
    }
    "read secret from file" in {
      escherConfig.secret("from-file-service2") shouldEqual "secret from file"
    }
    "throw error when file path is set but can't read" in {
      an[FileNotFoundException] should be thrownBy escherConfig.secret("wrong-file-service3")
    }
  }
}
