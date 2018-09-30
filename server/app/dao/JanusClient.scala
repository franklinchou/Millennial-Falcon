package dao

import com.google.inject.Singleton
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.janusgraph.core.schema.JanusGraphManagement

@Singleton
object JanusClient {

  private val graph = EntitlementGraph.graph

  // TODO Dynamically setup graph or do nothing if a graph already exists
  EntitlementGraph.setUp(graph)

  val jg: GraphTraversalSource = graph.traversal()

  def manager: JanusGraphManagement = graph.openManagement()

}
