package one.shn.brd.state

import one.shn.brd.input.Rating
import one.shn.brd.types._


case class Stats(
    users:    Registry[User],
    products: Registry[Product],
    ratings:  AggRatings,
    lastDay:  Long) {
  def aggregate(rating: Rating): Stats = {
    val (updatedUsers, userNum) = users register rating.user
    val (updatedProducts, productNum) = products register rating.product
    val updatedRatings = ratings aggregate (userNum, productNum, rating.score, rating.day)
    val updatedLastDay = if (rating.day > lastDay) rating.day else lastDay
    Stats(updatedUsers, updatedProducts, updatedRatings, updatedLastDay)
  }
}

object Stats {
  val empty = Stats(Registry(Map.empty, 0), Registry(Map.empty, 0), AggRatings(Map.empty), 0)
}

case class Registry[A](map: Map[A, Int], max: Int) {
  def register(a: A): (Registry[A], Int) =
    map get a match {
      case Some(n) => (this, n)
      case None    => (Registry(map + (a -> max), max + 1), max)
    }
}

case class AggRatings(map: Map[(Int, Int), (Float, Long)]) {
  import AggRatings._
  def aggregate(user: Int, product: Int, score: Float, day: Long): AggRatings =
    map get (user, product) match {
      case Some((aggScore, lastDay)) if lastDay > day =>
        AggRatings(map updated ((user, product), (addWithPenalty(aggScore, score, lastDay - day), lastDay)))
      case Some((aggScore, lastDay)) =>
        AggRatings(map updated ((user, product), (addWithPenalty(score, aggScore, day - lastDay), day)))
      case None =>
        AggRatings(map + ((user, product) -> (score, day)))
    }
}

object AggRatings {
  private val penalties: Stream[Float] = 1 #:: (penalties map (_ * 0.95F))
  def addWithPenalty(latterScore: Float, formerScore: Float, daysInBetween: Long): Float =
    latterScore + formerScore * penalties(daysInBetween.toInt)
}
