package dao

import com.google.inject.Singleton
import com.typesafe.config.ConfigFactory
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

@Singleton
object JanusClient {

  val backend = ConfigFactory.load.getString("storage.backend")

  val host = ConfigFactory.load.getString("storage.hostname")

  val env = ConfigFactory.load.getString("env")

  private val graph = JanusClientUtils.whichGraph(env)

  JanusClientUtils.setUp(graph)

  val jg: GraphTraversalSource = graph.traversal()

}
