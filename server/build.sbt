name := """millennial-falcon"""
organization := "fmc"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

val janusVersion = "0.3.0"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.0.0",
  "org.janusgraph" % "janusgraph-core" % janusVersion,
  "org.janusgraph" % "janusgraph-cql" % janusVersion
)


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "fmc.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "fmc.binders._"
