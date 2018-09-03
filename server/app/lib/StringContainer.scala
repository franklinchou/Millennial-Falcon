package lib

import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, Reads, Writes}

object StringContainer {

  /**
    * Json format
    *
    * @tparam A
    * @return
    */
  implicit def jsFormat[A]: Format[StringContainer[A]] = Format(
    Reads.StringReads.map(StringContainer.apply[A]),
    Writes.StringWrites.contramap(_.value)
  )

}


final case class StringContainer[A](value: String) extends AnyVal {

  override def toString: String = value

}
