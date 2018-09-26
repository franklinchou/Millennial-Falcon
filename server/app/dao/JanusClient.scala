package dao

import com.google.inject.Singleton
import com.typesafe.config.ConfigFactory
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.janusgraph.core.schema.JanusGraphManagement

@Singleton
object JanusClient {

  val env: String = ConfigFactory.load.getString("env")

  private val graph = EntitlementGraph.graph

  EntitlementGraph.setUp(graph)

  val jg: GraphTraversalSource = graph.traversal()

  def manager: JanusGraphManagement = graph.openManagement()

}
