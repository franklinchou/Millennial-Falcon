package dao

import java.util.UUID

import dao.JanusClient._
import models.Model
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.{Cardinality, JanusGraph, JanusGraphFactory}
import org.janusgraph.core.schema.SchemaAction
import org.janusgraph.graphdb.database.management.ManagementSystem

object JanusClientUtils {

  val testConfig = JanusGraphFactory.open("inmemory")

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
      .flatMap { k =>
        val vertexLabel = mgmt.getVertexLabel(k)
        Option(vertexLabel)
      }
      .foreach { vl =>
        mgmt.makeVertexLabel(vl.toString).make()
      }

    mgmt.commit()  // write to graph

    mgmt = jg.openManagement()

    val idProperty = mgmt.getPropertyKey(Model.Id)
    val nameProperty = mgmt.getPropertyKey(Model.Name)
    val typeProperty = mgmt.getPropertyKey(Model.Type)

    val user = mgmt.getVertexLabel(Model.UserType)
    val userGroup = mgmt.getVertexLabel(Model.UserGroupType)
    val product = mgmt.getVertexLabel(Model.ProductType)

    val indexLabels =
      Set[String](
        "id-index",
        "type-index",
        "id-type-index",
        "type-name-index",
        "clients-by-name-index",
        "users-by-name-index",
        "features-by-name-index"
      )

    // region Build indices

    mgmt.buildIndex("id-index", classOf[Vertex]).addKey(idProperty).unique().buildCompositeIndex()
    mgmt.buildIndex("type-index", classOf[Vertex]).addKey(typeProperty).buildCompositeIndex()


    mgmt
      .buildIndex("id-type-index", classOf[Vertex])
      .addKey(idProperty).addKey(typeProperty)
      .unique()
      .buildCompositeIndex()


    mgmt
      .buildIndex("type-name-index", classOf[Vertex])
      .addKey(typeProperty)
      .addKey(nameProperty)
      .buildCompositeIndex()

    mgmt
      .buildIndex("clients-by-name-index", classOf[Vertex])
      .addKey(typeProperty)
      .addKey(nameProperty)
      .indexOnly(userGroup)
      .unique()
      .buildCompositeIndex()

    mgmt
      .buildIndex("users-by-name-index", classOf[Vertex])
      .addKey(typeProperty)
      .addKey(nameProperty)
      .indexOnly(user)
      .unique()
      .buildCompositeIndex()

    mgmt
      .buildIndex("features-by-name-index", classOf[Vertex])
      .addKey(typeProperty)
      .addKey(nameProperty)
      .indexOnly(product)
      .unique()
      .buildCompositeIndex()

    mgmt.commit()

    // endregion

    indexLabels.par.foreach { il =>
      ManagementSystem.awaitGraphIndexStatus(jg, il).call()
    }

    mgmt = jg.openManagement()


    indexLabels.par.foreach { il =>
      mgmt.updateIndex(mgmt.getGraphIndex(il), SchemaAction.REINDEX).get()
    }

    mgmt.commit()

  }
}
