package dao

import com.google.inject.Singleton
import com.typesafe.config.ConfigFactory
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import play.api.Logger

import scala.concurrent.duration._

@Singleton
object JanusClient {

  private val graph = EntitlementGraph.graph

  val env = ConfigFactory.load.getString("env")

  val setup = ConfigFactory.load.getBoolean("setup")
  
  if (setup || env == "circle") {
    val start = System.nanoTime()
    Logger.info("Setting up graph...")
    EntitlementGraph.setUp(graph)
    val end = System.nanoTime()
    val elapsed = Duration(end - start, NANOSECONDS).toMillis
    Logger.info(s"Graph setup complete after $elapsed milliseconds")
  }

  val jg: GraphTraversalSource = graph.traversal()

}
