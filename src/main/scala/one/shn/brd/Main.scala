package one.shn.brd

import java.time.{LocalTime, ZoneId}

import one.shn.brd.input.Rating
import one.shn.brd.state.Stats

import scala.io.Source

object Main extends App {
  val inputPath = "/Users/shn/Downloads/xag.csv"
  def now = LocalTime now (ZoneId of "Europe/Paris")
  println(s"Opening file at $now.")
  val csv = Source fromFile inputPath
  println(s"Starting to read file at $now.")
  val ratings = csv.getLines map Rating.fromLine
  println(s"Starting processing data at $now.")
  val notifs = ratings.zipWithIndex map {
    case (r, n) if n % 10000 == 0 =>
      println(s"$n processed by $now.")
      r
    case (r, _) =>
      r
  }
  val stats = (notifs foldLeft Stats.empty)(_ aggregate _)
  println(s"Done reading file and processing data at $now.")
  csv.close
  println(s"File closed at $now.")
}
