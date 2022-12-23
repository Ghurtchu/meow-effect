package part3concurrency

import cats.effect._
import utils._

object Fibers extends IOApp.Simple {

  val meaningOfLife: IO[Int] = IO.pure(42)
  val favLang: IO[String] = IO.pure("Scala")

  def simpleIOComposition: IO[Unit] = for {
    _ <- meaningOfLife.debug // flatMap
    _ <- favLang.debug // map
  } yield ()

  // introduce Fiber
  // description of computation which will run on java thread managed by Cats-Effect runtime

  import cats.effect.Fiber

  def createFiber: Fiber[IO, Throwable, String] = ??? // almost impossible to create Fibers yourself

  // fiber allocation
  val aFiber: IO[Fiber[IO, Throwable, Int]] = meaningOfLife.debug.start

  def differentThreadIOs() = for {
    _ <- aFiber
    _ <- favLang.debug.start
  } yield ()

  // joining a fiber - waiting for that fiber to finish
  def runOnOtherThread[A](io: IO[A]): IO[Outcome[IO, Throwable, A]] = for {
    fib <- io.start
    res <- fib.join
  } yield res

  // IO[ResultType of fib.join]
  // fib.join = Outcome[IO, Throwable, A]

  // possible outcomes:
  // - success with an IO
  // - failure with an exception
  // - cancelled

  val someIO = runOnOtherThread(meaningOfLife)
  val resultFromAnotherThread = someIO.flatMap {
    case Outcome.Succeeded(eff) => eff
    case Outcome.Errored(_)     => IO.pure(0)
    case Outcome.Canceled()     => IO.pure(0)
  }

  def throwOnAnotherThread() = for {
    fib <- IO.raiseError[Int](new RuntimeException("no number for ya")).start
    res <- fib.join
  } yield res

  import scala.concurrent.duration._

  def testCancel() = {
//    val task = for {
//      _ <- IO("starting")
//      _ <- IO.sleep(1.second)
//      _ <- IO("done").debug
//    } yield ()
    val task = IO("starting").debug >> IO.sleep(1.second) >> IO("done").debug
    val onCancel = task.onCancel(IO("I am being cancelled").debug.void)

    for {
      fib <- onCancel.start
      _   <- IO.sleep(500.millis) >> IO.pure("cancelling").debug
      _   <- fib.cancel
      res <- fib.join
    } yield res
  }

  override def run: IO[Unit] = testCancel().debug.void

}
