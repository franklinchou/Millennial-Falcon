package dao

import com.google.inject.Singleton
import com.typesafe.config.ConfigFactory
import org.janusgraph.core.{JanusGraph, JanusGraphFactory}

@Singleton
object JanusClient {

  val backend = ConfigFactory.load.getString("storage.backend")

  val host = ConfigFactory.load.getString("storage.hostname")

  val env = ConfigFactory.load.getString("env")

  val testConfig = JanusGraphFactory.open("inmemory")

  val graph: JanusGraph = env match {
    case "prod" =>
      JanusGraphFactory
        .build  // TODO: Should use `open` to load typesafe configuration
        .set("storage.backend", backend)
        .set("storage.hostname", host)
        .open()
    case "test" =>
      testConfig
    case _ =>
      testConfig
  }


  /**
    * Set up graph
    */
  var mgmt = graph.openManagement()
  JanusClientUtils.setUp
  
}
