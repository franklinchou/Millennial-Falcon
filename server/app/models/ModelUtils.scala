package models

import org.apache.tinkerpop.gremlin.structure.Vertex

import scala.util.Try

object ModelUtils {

  def testVertex(v: Vertex, m: String): Boolean = {
    Try {
      v.value(m).asInstanceOf[String]
    }.toOption match {
      case Some(_) => true
      case None => false
    }
  }

}
