package part2effects

import cats.effect.*

import scala.concurrent.Future

object IOTraversal extends IOApp.Simple {

  import scala.concurrent.ExecutionContext.Implicits.global

  def heavyComputation(string: String): Future[Int] = Future {
    Thread.sleep(scala.util.Random.nextInt(1000))

    string.split(" ").length
  }

  val workload: List[String] = List(
    "I like CE",
    "I love Scala",
    "looking forward to some awesome stuff"
  )

  def clunkyFutures(): Unit = {
    val futures: List[Future[Int]] = workload.map(heavyComputation)
    // Future[List[Int]] would be hard to obtain

    // traverse
  }

  def traverseFutures(): Unit = {

    import cats.Traverse
    import cats.instances.list._ // instance for type classes on list
    val singleFuture: Future[List[Int]] = Traverse[List].traverse(workload)(heavyComputation)

    singleFuture.foreach(println)

  }

  import utils._

  def computeAsIO(string: String): IO[Int] = IO.delay {
    Thread.sleep(scala.util.Random.nextInt(1000))

    string.split(" ").length
  }.debug

  val ios: List[IO[Int]] = workload.map(computeAsIO)

  import cats.Traverse
  import cats.instances.list._

  val singleIO: IO[List[Int]] = Traverse[List].traverse(workload)(computeAsIO)

  // parallel traversal
  import cats.syntax.parallel._
  val singleIOPar: IO[List[Int]] = workload.parTraverse(computeAsIO)

  // exercises
  def sequence[A](ios: List[IO[A]]): IO[List[A]] = Traverse[List].traverse(ios)(identity)

  val seq1 = sequence(List(IO.pure(4).debug, IO.pure(10).debug))

  def sequenceV2[F[_]: Traverse, A](ios: F[IO[A]]): IO[F[A]] =
    Traverse[F].traverse(ios)(identity)

  val seq2 = sequenceV2[Option, String](Option(IO.pure("ssss").debug))

  def parSeq[A](ios: List[IO[A]]): IO[List[A]] = {
    import cats.syntax.parallel._
    ios.parTraverse(identity)
  }

  val parSeq1 = parSeq(List(IO.pure(1).debug, IO.pure(2).debug))

  override def run: IO[Unit] = parSeq1.void
}
