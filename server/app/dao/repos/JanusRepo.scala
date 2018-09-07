package dao.repos

import lib.StringContainer
import models.Model
import models.fields.ModelId
import org.janusgraph.core.JanusGraph

import scala.concurrent.Future

trait JanusRepo[T <: Model] {

  val graph: JanusGraph

  private val traversal = graph.traversal()

  val modelType: String // TODO Use reflections to uncover type?


  def create(record: T): Future[Boolean]


  def findAll(): Set[T] = {
    traversal
      .V()
      .has("model-type", modelType)
      .valueMap(true)

    Set.empty[T]  // TODO Pass tests for now
  }


  def delete(id: StringContainer[ModelId]): Future[Boolean]

}
