package services

import com.google.inject.ImplementedBy
import models.vertex._
import org.apache.tinkerpop.gremlin.structure.Vertex

import scala.concurrent.{ExecutionContext, Future}


@ImplementedBy(classOf[FeatureServiceJanus])
abstract class FeatureService()(implicit ec: ExecutionContext) {

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

}
