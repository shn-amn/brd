package one.shn.brd.state

import java.util.concurrent.atomic.AtomicInteger

import scala.collection.parallel.mutable.ParHashMap

trait MutableRegistry[A] {
  val mutableMap: ParHashMap[A, Int] = ParHashMap()
  val next:       AtomicInteger      = new AtomicInteger(0)

  def register(a: A): Int = mutableMap get a match {
    case Some(n) => n
    case None    => val n = next.getAndIncrement; mutableMap put (a, n); n
  }
}
