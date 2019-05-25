package one.shn.brd.state

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicLong

import one.shn.brd.implicits._
import one.shn.brd.input.Rating
import one.shn.brd.types._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

object Stats {
  val lastDay: AtomicLong = new AtomicLong(0)

  def aggregate(rating: Rating): Unit = {
    val userNum = Users register rating.user
    val productNum = Products register rating.product
    Ratings aggregate (userNum, productNum, rating.score, rating.day)
    lastDay updateAndGet (d => if (d < rating.day) rating.day else d)
  }

  def startWritingIn(basePath: String)(implicit ec: ExecutionContext): Future[Unit] = {
    def path(filename: String) = Paths get (basePath, s"$filename.csv") toString
    def writer(filename: String) = Future {
      val file = new File(path(filename))
      if (!file.exists) file.createNewFile()
      new BufferedWriter(new FileWriter(file))
    }
    Future sequence List(
      writer("lookupusers") map (Users.mutableMap write (_, (user, num) => s"$user,$num\n")) map (_.close),
      writer("lookupproducts") map (Products.mutableMap write (_, (product, num) => s"$product,$num\n")) map (_.close),
      writer("aggratings") map (Ratings.mutableMap write (_, Ratings encodeAt lastDay.get)) map (_.close)) map (_ => ())
  }
}

object Users extends MutableRegistry[User]
object Products extends MutableRegistry[Product]

object Ratings extends MutableAggregation[(Int, Int), (Float, Long)] {
  val penalties: Stream[Float] = 1 #:: (penalties map (_ * 0.95F))

  val addWithPenalties: ((Float, Long), (Float, Long)) => (Float, Long) = {
    case ((oldValue, oldDay), (newValue, newDay)) if oldDay < newDay =>
      (newValue + oldValue * penalties(newDay - oldDay toInt), newDay)
    case ((newValue, newDay), (oldValue, oldDay)) =>
      (newValue + oldValue * penalties(newDay - oldDay toInt), newDay)
  }

  def aggregate(user: Int, product: Int, score: Float, day: Long): Unit =
    aggregate((user, product), (score, day))(addWithPenalties)

  def encodeAt(lastDay: Long): ((Int, Int), (Float, Long)) => String = {
    case ((user, product), (score, day)) if score * penalties(lastDay - day toInt) >= 0.01 =>
      s"$user,$product,${score * penalties(lastDay - day toInt)}\n"
    case _ =>
      ""
  }
}
