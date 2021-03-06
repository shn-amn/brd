package one.shn.brd.input

import java.util.UUID

import one.shn.brd.types._

import scala.concurrent.duration._
import scala.language.postfixOps

case class Rating(user: User, product: Product, score: Float, day: Long)

object Rating {
  def fromLine(line: String): Rating = {
    val str = line split ","
    Rating(
      user    = UUID fromString str(0),
      product = str(1),
      score   = str(2) toFloat,
      day    =  str(3).toLong.milliseconds toDays)
  }


}