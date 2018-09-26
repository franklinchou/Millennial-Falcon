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

  mgmt.commit()  // commit index status as disabled
  graph.tx().commit()

  // block until all the indices are disabled
  indices.foreach { k =>
    ManagementSystem.awaitGraphIndexStatus(graph, k).status(SchemaStatus.DISABLED).call()
  }

  mgmt = graph.openManagement()

  indices.foreach { k =>
    val index = mgmt.getGraphIndex(k)
    mgmt.updateIndex(index, SchemaAction.REMOVE_INDEX)
  }

  mgmt.commit()  // commit index status as disabled
  graph.tx().commit()

  System.exit(0)

}
