package services

import dao.JanusClient.jg
import javax.inject.Inject
import models.vertex.{FeatureModel, FeatureType, Type}
import play.api.Logger

// This is needed in order to turn java list into scala list for `map`
import utils.ListConversions._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}


class FeatureServiceJanus @Inject()()
                                   (implicit ec: ExecutionContext) extends FeatureService {

  def findAllFeatures: Future[List[FeatureModel]] = {
    Try {
      jg
        .V()
        .hasLabel(FeatureType)
        .has(Type, FeatureType)
        .toList
        .map(v => v: FeatureModel)  // Uses `ListConversions`
    } match {
      case Success(features) => Future { features }
      case Failure(e) =>
        Logger.error(s"`findAllFeatures` failed with error $e")
        Future { List.empty[FeatureModel ]}
    }
  }

}
