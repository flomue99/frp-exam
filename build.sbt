ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "3.7.3"
ThisBuild / scalacOptions := Seq("-unchecked", "-feature", "-deprecation")

val pekkoVersion = "1.3.0"
lazy val root = (project in file("."))
  .settings(
    name := "Klausur",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
      "org.apache.pekko" %% "pekko-pki" % pekkoVersion,

    )
  )
