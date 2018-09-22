name := """millennial-falcon"""
organization := "fmc"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

/**
  * Latest version = 0.3.0, but results in a `ClassNotFoundException` with
  * `tinkerpop.server.GraphManager`. This appears to be known and fixed; waiting
  * on bump to 0.3.0+
  */
val janusVersion = "0.2.1"

libraryDependencies ++= Seq(
  guice,
  "com.michaelpollmeier" %% "gremlin-scala" % "3.3.3.3",
  "org.apache.tinkerpop" % "gremlin-driver" % "3.3.3",
  "org.janusgraph" % "janusgraph-core" % janusVersion,
  "org.janusgraph" % "janusgraph-cql" % janusVersion,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.mockito" % "mockito-core" % "2.7.22",
  // "com.datastax.cassandra" % "cassandra-driver-core" % "3.0.0",
  "ai.x" %% "play-json-extensions" % "0.10.0"
)

resolvers ++= Seq(
  Resolver.mavenLocal,
  "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/public",
)


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "fmc.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "fmc.binders._"
