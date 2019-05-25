package one.shn.brd.state

import scala.collection.parallel.mutable.ParHashMap

trait MutableAggregation[K, V] {
  val mutableMap: ParHashMap[K, V] = ParHashMap()

  def aggregate(k: K, v: V)(f: (V, V) => V): Unit =
    mutableMap get k match {
      case Some(existing) => mutableMap update (k, f(existing, v))
      case None           => mutableMap put (k, v)
    }
}
