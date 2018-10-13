package dao

import java.time.temporal.ChronoUnit
import java.util.UUID

import com.typesafe.config.ConfigFactory
import models.vertex
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.schema.{SchemaAction, SchemaStatus}
import org.janusgraph.core.{Cardinality, JanusGraph, JanusGraphFactory, VertexLabel}
import org.janusgraph.graphdb.database.management.ManagementSystem

object EntitlementGraph {

  val backend: String = ConfigFactory.load.getString("storage.backend")

  val host: String = ConfigFactory.load.getString("storage.hostname")

  val env = ConfigFactory.load.getString("env")

  val graph: JanusGraph = {
    
    if (env == "circle") {
      JanusGraphFactory.open("inmemory")
    } else {
      JanusGraphFactory
        .build
        .set("storage.backend", backend)
        .set("storage.hostname", host)
        .open()
    }
  }


  // Set up the Janus graph
  def setUp(jg: JanusGraph): Unit = {

    var mgmt = jg.openManagement()

    val properties =
      Map(
        vertex.Id -> classOf[UUID],
        vertex.Name -> classOf[String],
        vertex.Type -> classOf[String]
      )

    // Make property keys for any key that doesn't already exist
    properties
      .filter(pk => Option(mgmt.getPropertyKey(pk._1)).isEmpty)
      .foreach(pk => mgmt.makePropertyKey(pk._1).cardinality(Cardinality.SINGLE).dataType(pk._2).make())

    // Make vertex labels for any key that doesn't already exist
    keys
      .filter(k => Option(mgmt.getVertexLabel(k)).isEmpty)
      .foreach(vl => mgmt.makeVertexLabel(vl.toString).make())

    mgmt.commit()  // write to graph

    mgmt = jg.openManagement()  // re-assignment in order for "open management" command to take

    val idProperty = mgmt.getPropertyKey(vertex.Id)
    val typeProperty = mgmt.getPropertyKey(vertex.Type)
    val nameProperty = mgmt.getPropertyKey(vertex.Name)

    val userLabel: VertexLabel = mgmt.getVertexLabel(vertex.UserType)
    val groupLabel = mgmt.getVertexLabel(vertex.GroupType)
    val productLabel = mgmt.getVertexLabel(vertex.FeatureType)


    // region Build indices

    mgmt
      .buildIndex(dao.byIdComposite, classOf[Vertex])
      .addKey(idProperty)
      .unique()
      .buildCompositeIndex()

    // `findAll` index
    mgmt
      .buildIndex(dao.byTypeComposite, classOf[Vertex])
      .addKey(typeProperty)
      .buildCompositeIndex()

    mgmt
      .buildIndex(dao.byIdTypeComposite, classOf[Vertex])
      .addKey(typeProperty)
      .addKey(idProperty)
      .unique()
      .buildCompositeIndex()

    mgmt
      .buildIndex(dao.byTypeNameComposite, classOf[Vertex])
      .addKey(typeProperty)
      .addKey(nameProperty)
      .buildCompositeIndex()

    // Index group by name
    mgmt
      .buildIndex(dao.byGroupNameComposite, classOf[Vertex])
      .addKey(typeProperty)
      .addKey(nameProperty)
      .indexOnly(groupLabel)
      .unique()
      .buildCompositeIndex()

    // Index users by name
    mgmt
      .buildIndex(dao.byUserNameComposite, classOf[Vertex])
      .addKey(nameProperty)
      .indexOnly(userLabel)
      .unique()
      .buildCompositeIndex()

    // Index products by name
    mgmt
      .buildIndex(dao.byFeatureNameComposite, classOf[Vertex])
      .addKey(typeProperty)
      .addKey(nameProperty)
      .indexOnly(productLabel)
      .unique()
      .buildCompositeIndex()

    mgmt.commit()  // commit indices

    // Block until the index is ready
    indices.foreach { k =>
      ManagementSystem
        .awaitGraphIndexStatus(jg, k)
        .status(SchemaStatus.REGISTERED)
        .timeout(2, ChronoUnit.MINUTES)
        .call()
    }

    mgmt = jg.openManagement()

    indices.foreach { il =>
      mgmt.updateIndex(mgmt.getGraphIndex(il), SchemaAction.REINDEX).get()
    }

    mgmt.commit()

    // endregion

  }
}
