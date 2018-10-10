package services

import dao.JanusClient.jg
import javax.inject.Inject
import models.vertex
import models.vertex.FeatureModel
import org.apache.tinkerpop.gremlin.structure.Vertex
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
        .hasLabel(vertex.FeatureType)
        .has(vertex.Type, vertex.FeatureType)
        .toList
        .map(v => v: FeatureModel)  // Uses `ListConversions`
    } match {
      case Success(features) => Future { features }
      case Failure(e) =>
        Logger.error(s"`findAllFeatures` failed with error $e")
        Future { List.empty[FeatureModel ]}
    }
  }

  /**
    * Add a new [[FeatureModel]] to the graph
    *
    * @param fm
    * @return
    */
  def add(fm: FeatureModel): Vertex = {
    val result: Vertex =
      jg
        .addV(fm.`type`)
        .property(vertex.Type, fm.`type`)
        .property(vertex.Name, fm.name.value)
        .property(vertex.Id, fm.id.value)
        .property(vertex.CreatedAt, fm.createdAt.toString)
        .property(vertex.ModifiedAt, fm.modifiedAt.toString)
        .next()

    val _ = jg.tx.commit()
    result
  }
}
