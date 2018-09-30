package utils

import java.util.{List => JavaList}
import scala.collection.JavaConverters._


object ListConversions {

  implicit def javaList2Scala[A](javaList: JavaList[A]): List[A] = {
    javaList.asScala.toList
  }

}
