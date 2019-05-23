package one.shn.brd.input

import java.time.Instant
import java.util.UUID

case class Rating(user: UUID, product: String, score: Float, time: Instant)

object Rating {
  def fromLine(line: String): Rating = {
    val str = line split ","
    Rating(
      user    = UUID fromString str(0),
      product = str(1),
      score   = str(2).toFloat,
      time    = Instant ofEpochMilli str(3).toLong)
  }
}