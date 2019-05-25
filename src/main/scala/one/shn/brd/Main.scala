package one.shn.brd

import java.time.{LocalTime, ZoneId}

import one.shn.brd.input.Rating
import one.shn.brd.state.{Products, Ratings, Stats, Users}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import scala.util.{Failure, Success}

object Main extends App {
  val inputPath = "/Users/shn/Downloads/xag.csv"
  def now = LocalTime now (ZoneId of "Europe/Paris")
  println(s"Opening file at $now.")
  val csv = Source fromFile inputPath
  println(s"Starting to read file at $now.")
  val ratings = csv.getLines map Rating.fromLine
  println(s"Starting processing data at $now.")
  val notifs = ratings.zipWithIndex map {
    case (r, n) if n % 1000000 == 0 => println(s"$n processed by $now."); r
    case (r, _)                     => r
  }
  notifs foreach Stats.aggregate
  println(s"Done reading file and processing data at $now.")
  csv.close
  println(s"File closed at $now.")
  println(s"${Users.mutableMap.size} users, ${Products.mutableMap.size} products and ${Ratings.mutableMap.size} ratings identified.")
  println(s"Starting to write files at $now.")
  Stats startWritingIn "/Users/shn/tmp" andThen {
    case Success(_) => println(s"Done writing files at $now.")
    case Failure(e) => println(s"Failed at $now:"); e.printStackTrace()
  }
}
