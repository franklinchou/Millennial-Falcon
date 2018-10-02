package dao

import com.google.inject.Singleton
import com.typesafe.config.ConfigFactory
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

@Singleton
object JanusClient {

  private val graph = EntitlementGraph.graph

  val env = ConfigFactory.load.getString("env")

  /**
    * Remove warnings on using indices when running tests.
    */
  if (env == "circle") {
    EntitlementGraph.setUp(graph)
  }

  // Uncomment this line if setting up graph on a new instance
  // Re-comment once the graph is setup.
  // TODO Hacky
  // EntitlementGraph.setUp(graph)


  val jg: GraphTraversalSource = graph.traversal()

}
