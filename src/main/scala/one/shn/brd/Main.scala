package one.shn.brd

import one.shn.brd.input.Rating

import scala.io.Source

object Main extends App {
  val inputPath = "/Users/shn/Downloads/xag.csv"
  val csv = Source fromFile inputPath
  csv.getLines map Rating.fromLine foreach println
  println("Parsing works.")
}
