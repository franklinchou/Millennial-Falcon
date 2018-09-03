package dao.repos

import java.util

import gremlin.scala.Vertex
import lib.StringContainer
import models.Model
import models.fields.ModelId
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.janusgraph.core.JanusGraph

import scala.concurrent.Future

trait JanusRepo[T <: Model] {


  val graph: JanusGraph

  val modelType: String // TODO Use reflections to uncover type?

  private val vertices: GraphTraversal[Vertex, util.List[Vertex]] = {
    val g = graph.traversal()
    val vertexIterator: GraphTraversal[Vertex, Vertex] = g.V().has("modelType", modelType)
    g.close()

    vertexIterator.fold()
  }



  def create(record: T): Future[Boolean]


  def findAll(): Set[T] = {
    val v = vertices


    Set.empty[T]
  }


  def upsert(record: T): Future[Boolean]


  def delete(id: StringContainer[ModelId]): Future[Boolean]

}
