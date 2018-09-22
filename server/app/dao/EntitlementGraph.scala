package dao

import java.util.UUID

import dao.JanusClient._
import models.Model
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.schema.SchemaAction
import org.janusgraph.core.{Cardinality, JanusGraph, JanusGraphFactory}
import org.janusgraph.graphdb.database.management.ManagementSystem

object EntitlementGraph {

  val testConfig: JanusGraph = JanusGraphFactory.open("inmemory")

  def whichGraph(env: String): JanusGraph = {
    env match {
      case "prod" =>
        JanusGraphFactory
          .build // TODO: Should use `open` to load typesafe configuration
          .set("storage.backend", backend)
          .set("storage.hostname", host)
          .open()
      case "test" =>
        testConfig
      case _ =>
        testConfig
    }
  }


  // Set up the Janus graph
  def setUp(jg: JanusGraph): Unit = {

    var mgmt = jg.openManagement()

    mgmt.makePropertyKey(Model.Id).cardinality(Cardinality.SINGLE).dataType(classOf[UUID]).make()
    mgmt.makePropertyKey(Model.Name).cardinality(Cardinality.SINGLE).dataType(classOf[String]).make()
    mgmt.makePropertyKey(Model.Type).cardinality(Cardinality.SINGLE).dataType(classOf[String]).make()

    keys
      .filter(k => Option(mgmt.getVertexLabel(k)).isEmpty)
      .foreach(vl => mgmt.makeVertexLabel(vl.toString).make())

    mgmt.commit()  // write to graph

    mgmt = jg.openManagement()  // re-assignment in order for "open management" command to take

    val idProperty = mgmt.getPropertyKey(Model.Id)
    val typeProperty = mgmt.getPropertyKey(Model.Type)
    val nameProperty = mgmt.getPropertyKey(Model.Name)

    val user = mgmt.getVertexLabel(Model.UserType)
    val userGroup = mgmt.getVertexLabel(Model.UserGroupType)
    val product = mgmt.getVertexLabel(Model.ProductType)


    // region Build indices

    mgmt
      .buildIndex(dao.idIndex, classOf[Vertex])
      .addKey(idProperty)
      .unique()
      .buildCompositeIndex()

    mgmt
      .buildIndex(dao.typeIndex, classOf[Vertex])
      .addKey(typeProperty)
      .buildCompositeIndex()

    mgmt
      .buildIndex(dao.idTypeIndex, classOf[Vertex])
      .addKey(typeProperty)
      .addKey(idProperty)
      .unique()
      .buildCompositeIndex()

    mgmt
      .buildIndex(dao.typeNameIndex, classOf[Vertex])
      .addKey(typeProperty)
      .addKey(nameProperty)
      .buildCompositeIndex()


    // Index user-group by name
    mgmt
      .buildIndex(dao.groupNameIndex, classOf[Vertex])
      .addKey(typeProperty)
      .addKey(nameProperty)
      .indexOnly(userGroup)
      .unique()
      .buildCompositeIndex()

    // Index users by name
    mgmt
      .buildIndex(dao.userNameIndex, classOf[Vertex])
      .addKey(typeProperty)
      .addKey(nameProperty)
      .indexOnly(user)
      .unique()
      .buildCompositeIndex()

    // Index products by name
    mgmt
      .buildIndex(dao.productNameIndex, classOf[Vertex])
      .addKey(typeProperty)
      .addKey(nameProperty)
      .indexOnly(product)
      .unique()
      .buildCompositeIndex()

    mgmt.commit()

    // endregion

    indexKeys.foreach { il =>
      ManagementSystem.awaitGraphIndexStatus(jg, il).call()
    }

    mgmt = jg.openManagement()

    indexKeys.foreach { il =>
      mgmt.updateIndex(mgmt.getGraphIndex(il), SchemaAction.REINDEX).get()
    }

    mgmt.commit()

  }
}
