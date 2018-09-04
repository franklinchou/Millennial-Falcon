package dao

import java.util.UUID

import com.google.inject.Singleton
import com.typesafe.config.ConfigFactory
import models.Model
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.schema.SchemaAction
import org.janusgraph.core.{Cardinality, JanusGraph, JanusGraphFactory}
import org.janusgraph.graphdb.database.management.ManagementSystem

@Singleton
object JanusClient {

  val backend = ConfigFactory.load.getString("storage.backend")

  val host = ConfigFactory.load.getString("storage.hostname")

  val env = ConfigFactory.load.getString("env")

  val testConfig = JanusGraphFactory.open("inmemory")

  val graph: JanusGraph = env match {
    case "prod" =>
      JanusGraphFactory
        .build  // TODO: Should use `open` to load typesafe configuration
        .set("storage.backend", backend)
        .set("storage.hostname", host)
        .open()
    case "test" =>
      testConfig
    case _ =>
      testConfig
  }


  /**
    * Set up graph
    *
    *
    */
  var mgmt = graph.openManagement()


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


  graph.openManagement()

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
      "clients-by-name",
      "users-by-name",
      "features-by-name"
    )


  mgmt.buildIndex("id-index", classOf[Vertex]).addKey(idProperty).unique().buildCompositeIndex()
  mgmt.buildIndex("type-index", classOf[Vertex]).addKey(typeProperty).buildCompositeIndex()
  mgmt.buildIndex("id-type-index", classOf[Vertex]).addKey(idProperty).addKey(typeProperty).unique().buildCompositeIndex()
  mgmt.buildIndex("type-name-index", classOf[Vertex]).addKey(typeProperty).addKey(nameProperty).buildCompositeIndex()



  mgmt.buildIndex("clients-by-name", classOf[Vertex]).addKey(typeProperty).addKey(nameProperty).indexOnly(userGroup).unique().buildCompositeIndex()
  mgmt.buildIndex("users-by-name", classOf[Vertex]).addKey(typeProperty).addKey(nameProperty).indexOnly(user).unique().buildCompositeIndex()
  mgmt.buildIndex("features-by-name", classOf[Vertex]).addKey(typeProperty).addKey(nameProperty).indexOnly(product).unique().buildCompositeIndex()

  mgmt.commit()

  indexLabels.par.foreach { il =>
    ManagementSystem.awaitGraphIndexStatus(graph, il).call()
  }

  mgmt = graph.openManagement()


  indexLabels.par.foreach { il =>
    mgmt.updateIndex(mgmt.getGraphIndex(il), SchemaAction.REINDEX).get()
  }


  mgmt.commit()

}
