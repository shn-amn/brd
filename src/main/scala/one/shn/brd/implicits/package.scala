package one.shn.brd

import java.io.Writer
import java.time.{LocalDateTime, ZoneId}

import scala.collection.parallel.ParMap

package object implicits {

  implicit class LazyIteratorUtils[+A](iterator: Iterator[A]) {
    // Applies `effect` to all elements of the iterator without leaving iterator intact.
    def tap(effect: A => Unit): Iterator[A] = iterator map { a => effect(a); a }
    // The same as `tap(A => Unit)` but with a partial effect.
    def partialTap(effect: PartialFunction[A, Unit]): Iterator[A] = tap(a => effect applyOrElse (a, (_: A) => ()))
    // At every `iterations` iterations, applies `effect`.
    def tapEvery(iterations: Int, effect: (A, Int) => Unit): Iterator[A] =
      iterator.zipWithIndex partialTap { case (a: A, i: Int) if i % iterations == 0 => effect(a, i) } map (_._1)
    def logProgressEvery(lines: Int): Iterator[A] =
      tapEvery(lines, (_, i) => println(s"$i lines processed by $now"))
    private def now = LocalDateTime now (ZoneId of "Europe/Paris")
  }

  implicit class MapWriter[K, V](map: ParMap[K, V]) {
    def write[W <: Writer](writer: W, encode: (K, V) => String): W = {
      map foreach (encode.tupled andThen writer.write)
      writer
    }
  }

}
