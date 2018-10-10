package services

import com.google.inject.ImplementedBy
import models.vertex._

import scala.concurrent.Future


@ImplementedBy(classOf[FeatureServiceJanus])
abstract class FeatureService {

  /**
    * Find all the features in the graph
    *
    * @return
    */
  def findAllFeatures: Future[List[FeatureModel]]

}
