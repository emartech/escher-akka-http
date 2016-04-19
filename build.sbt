
name := "escher-akka-http"
organization  := "com.emarsys"
version       := "0.0.6"

scalaVersion := "2.11.7"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaStreamV      = "2.0.2"
  val scalaTestV       = "3.0.0-M7"
  Seq(
    "com.typesafe.akka"       %% "akka-http-core-experimental"       % akkaStreamV,
    "com.typesafe.akka"       %% "akka-http-experimental"            % akkaStreamV,
    "com.typesafe.akka"       %% "akka-http-spray-json-experimental" % akkaStreamV,
    "org.scalatest"           %% "scalatest"                         % scalaTestV       % "test",
    "com.emarsys"             %  "escher"                            % "0.3"
  )
}

publishTo := Some(Resolver.file("releases", new File("releases")))
