package utils

import dao.{EntitlementGraph, indices}
import org.janusgraph.core.schema.{SchemaAction, SchemaStatus}
import org.janusgraph.graphdb.database.management.ManagementSystem

object ClearGraph extends App {

  val graph = EntitlementGraph.graph

  var mgmt = graph.openManagement()

  // remove indices

  // first set the index status to disable
  dao
    .indices
    .foreach { i =>
      val index = mgmt.getGraphIndex(i)
      mgmt.updateIndex(index, SchemaAction.DISABLE_INDEX).get()
    }

  // block until all the indices are disabled
  indices.foreach { k =>
    ManagementSystem.awaitGraphIndexStatus(graph, k).status(SchemaStatus.DISABLED).call()
  }

  mgmt.commit()  // commit index status as disabled

  System.exit(0)

}
