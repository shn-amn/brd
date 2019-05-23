package one.shn

import scala.io.Source

object Main extends App {
  val inputPath = "/Users/shn/Downloads/xag.csv"
  val csv = Source fromFile inputPath
  val lineCount = csv.getLines.foldLeft(0)((c, _) => c + 1)
  println(lineCount)
}
