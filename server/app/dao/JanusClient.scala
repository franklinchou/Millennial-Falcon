package dao

import com.google.inject.Singleton
import com.typesafe.config.ConfigFactory
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import play.api.Logger

@Singleton
object JanusClient {

  private val graph = EntitlementGraph.graph

  val env = ConfigFactory.load.getString("env")

  val setup = ConfigFactory.load.getBoolean("setup")
  
  if (setup || env == "circle") {
    Logger.info("Setting up graph...")
    EntitlementGraph.setUp(graph)
    Logger.info("Graph setup complete.")
  }

  val jg: GraphTraversalSource = graph.traversal()

}
