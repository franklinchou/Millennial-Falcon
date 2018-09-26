package dao

import java.util.UUID

import com.typesafe.config.ConfigFactory
import models.Model
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.schema.SchemaAction
import org.janusgraph.core.{Cardinality, JanusGraph, JanusGraphFactory, VertexLabel}
import org.janusgraph.graphdb.database.management.ManagementSystem

object EntitlementGraph {

  val backend = ConfigFactory.load.getString("storage.backend")

  val host = ConfigFactory.load.getString("storage.hostname")

  val graph: JanusGraph =
    JanusGraphFactory
      .build
      .set("storage.backend", backend)
      .set("storage.hostname", host)
      .open()

  // Set up the Janus graph
  def setUp(jg: JanusGraph): Unit = {

    var mgmt = jg.openManagement()

    mgmt.makePropertyKey(Model.Id).cardinality(Cardinality.SINGLE).dataType(classOf[UUID]).make()
    mgmt.makePropertyKey(Model.Name).cardinality(Cardinality.SINGLE).dataType(classOf[String]).make()
    mgmt.makePropertyKey(Model.Type).cardinality(Cardinality.SINGLE).dataType(classOf[String]).make()

    // Make vertex labels for all keys
    keys
      .filter(k => Option(mgmt.getVertexLabel(k)).isEmpty)
      .foreach(vl => mgmt.makeVertexLabel(vl.toString).make())

    mgmt.commit()  // write to graph

    mgmt = jg.openManagement()  // re-assignment in order for "open management" command to take

    val idProperty = mgmt.getPropertyKey(Model.Id)
    val typeProperty = mgmt.getPropertyKey(Model.Type)
    val nameProperty = mgmt.getPropertyKey(Model.Name)

    val userLabel: VertexLabel = mgmt.getVertexLabel(Model.UserType)
    val groupLabel = mgmt.getVertexLabel(Model.GroupType)
    val productLabel = mgmt.getVertexLabel(Model.ProductType)


    // region Build indices

    mgmt
      .buildIndex(dao.byIdComposite, classOf[Vertex])
      .addKey(idProperty)
      .unique()
      .buildCompositeIndex()

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


    // Index user-group by name
    mgmt
      .buildIndex(dao.byGroupNameComposite, classOf[Vertex])
      .addKey(typeProperty)
      .addKey(nameProperty)
      .indexOnly(groupLabel)
      .unique()
      .buildCompositeIndex()

    // Index users by name
    mgmt
      .buildIndex(dao.userNameComposite, classOf[Vertex])
      .addKey(nameProperty)
      .indexOnly(userLabel)
      .unique()
      .buildCompositeIndex()

    // Index products by name
    mgmt
      .buildIndex(dao.productNameComposite, classOf[Vertex])
      .addKey(typeProperty)
      .addKey(nameProperty)
      .indexOnly(productLabel)
      .unique()
      .buildCompositeIndex()

    mgmt.commit()  // commit indices

    // endregion

    // Block until the index is ready
    indices.foreach { k =>
      ManagementSystem.awaitGraphIndexStatus(jg, k).call()
    }

    mgmt = jg.openManagement()

    indices.foreach { il =>
      mgmt.updateIndex(mgmt.getGraphIndex(il), SchemaAction.REINDEX).get()
    }

    mgmt.commit()

  }
}
