package services

import com.google.inject.ImplementedBy
import lib.StringContainer
import models.field.IdField
import models.vertex._
import org.apache.tinkerpop.gremlin.structure.Vertex

import scala.concurrent.{ExecutionContext, Future}


@ImplementedBy(classOf[FeatureServiceJanus])
abstract class FeatureService()(implicit ec: ExecutionContext) {

  /**
    * Given the feature id, find the associated vertex
    *
    * @param id
    * @return
    */
  def findVertex(id: StringContainer[IdField]): Option[Vertex]


  /**
    * Safe find a feature by its id
    *
    * @param id
    * @return
    */
  def find(id: StringContainer[IdField]): Future[Option[FeatureModel]]


  /**
    * Find all the features in the graph
    *
    * @return
    */
  def findAllFeatures: Future[List[FeatureModel]]

  /**
    * Add a new [[FeatureModel]] to the graph
    *
    * @param fm
    * @return
    */
  def add(fm: FeatureModel): Vertex

  /**
    * Given an id, remove the feature from the graph
    *
    * @param id
    * @return
    */
  def remove(id: StringContainer[IdField]): Boolean

}
