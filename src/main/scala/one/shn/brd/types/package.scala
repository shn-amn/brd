package one.shn.brd

import java.time.{Instant, LocalDateTime, ZoneId}
import java.util.UUID

package object types {
  type User = UUID
  type Product = String

  implicit class InstantToEpochDay(time: Instant) {
    def toEpochDay: Long = LocalDateTime.ofInstant(time, ZoneId of "Europe/Paris").toLocalDate.toEpochDay
  }
}
