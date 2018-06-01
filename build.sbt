
val scalaV = "2.12.4"

name          := "escher-akka-http"
organization  := "com.emarsys"
version       := "0.2.1"

scalaVersion  := scalaV
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

lazy val ITest = config("it") extend Test
configs(ITest)
Defaults.itSettings

libraryDependencies ++= {
  val akkaHttpV  = "10.0.11"
  val scalaTestV = "3.0.1"
  Seq(
    "com.typesafe.akka" %% "akka-stream" % "2.5.9",
    "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.9"
  ) ++
  Seq(
    "com.typesafe.akka"  %% "akka-http-core"       % akkaHttpV,
    "com.typesafe.akka"  %% "akka-http"            % akkaHttpV,
    "com.typesafe.akka"  %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka"  %% "akka-http-testkit"    % akkaHttpV  % "test",
    "org.scalatest"      %% "scalatest"            % scalaTestV % "test",
    "com.emarsys"        %  "escher"               % "0.3") ++
  Seq(
    "io.gatling.highcharts" %  "gatling-charts-highcharts" % "2.3.0"          % "it,test",
    "io.gatling"            %  "gatling-test-framework"    % "2.3.0"          % "it,test"
  )

}

scalaVersion in ThisBuild := scalaV

publishTo := Some(Resolver.file("releases", new File("releases")))

enablePlugins(GatlingPlugin)
