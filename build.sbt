organization  := "com.emarsys"
name          := "escher-akka-http"
crossScalaVersions := List("2.13.3", "2.12.12")

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaHttpV  = "10.2.2"
  val akkaStreamV = "2.6.9"
  val scalaTestV = "3.2.2"
  Seq(
    "com.typesafe.akka"  %% "akka-http-core"       % akkaHttpV,
    "com.typesafe.akka"  %% "akka-http"            % akkaHttpV,
    "com.typesafe.akka"  %% "akka-http-testkit"    % akkaHttpV  % Test,
    "com.typesafe.akka"  %% "akka-stream"          % akkaStreamV,
    "com.typesafe.akka"  %% "akka-stream-testkit"  % akkaStreamV % Test,
    "org.scalatest"      %% "scalatest"            % scalaTestV % Test,
    "com.emarsys"        %  "escher"               % "0.3.4"
  )
}

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(List(
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  homepage := Some(url("https://github.com/emartech/escher-akka-http")),
  developers := List(
    Developer("andrasp3a", "Andras Papp", "andras.papp@emarsys.com", url("https://github.com/andrasp3a")),
    Developer("doczir", "Robert Doczi", "doczi.r@gmail.com", url("https://github.com/doczir")),
    Developer("gyfarkas", "Gyorgy Farkas", "gyfarkas@gmail.com", url("https://github.com/gyfarkas")),
    Developer("itsdani", "Daniel Segesdi", "daniel.segesdi@emarsys.com", url("https://github.com/itsdani")),
    Developer("jupposessho", "Vilmos Feher", "vilmos.feher@emarsys.com", url("https://github.com/jupposessho")),
    Developer("Ksisu", "Kristof Horvath", "kristof.horvath@emarsys.com", url("https://github.com/Ksisu")),
    Developer("laszlovaspal", "Laszlo Vaspal", "laszlo.vaspal@emarsys.com", url("https://github.com/laszlovaspal")),
    Developer("miklos-martin", "Miklos Martin", "miklos.martin@gmail.com", url("https://github.com/miklos-martin")),
    Developer("suliatis", "Attila Suli", "attila.suli@emarsys.com", url("https://github.com/suliatis")),
    Developer("tg44", "Gergo Torcsvari", "gergo.torcsvari@emarsys.com", url("https://github.com/tg44")),
    Developer("tt0th", "Tibor Toth", "tibor.toth@emarsys.com", url("https://github.com/tt0th"))
  )
))
