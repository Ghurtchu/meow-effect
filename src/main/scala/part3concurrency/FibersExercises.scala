package part3concurrency

import cats.effect.*
import cats.effect.kernel.Outcome.Succeeded
import utils.*

object FibersExercises extends IOApp.Simple {


  // ex1
  // function that runs an IO on another thread and depending on the result
  // - return the result of IO
  // - if errored or cancelled return failed IO
  def ex1[A](ioa: IO[A]): IO[A] = {

    import cats.effect.kernel.Outcome._

    val ioResult: IO[Outcome[IO, Throwable, A]] = for {
      fib <- ioa.start
      res <- fib.join
    } yield res

    ioResult.flatMap {
      case Succeeded(r) => r
      case Errored(e)   => IO.raiseError(e)
      case Canceled()   => IO.raiseError(new RuntimeException("Computation canceled."))
    }
  }

  import scala.concurrent.duration._

  val ex1program = ex1 {
    IO.delay("starting").debug >> IO.sleep(1.second) >> IO.delay(42 / 0).debug
  }.void

  def ex2[A, B](ioa: IO[A], iob: IO[B]): IO[(A, B)] = {

    import cats.effect.kernel.Outcome._

    type Out[T] = Outcome[IO, Throwable, T]

    val res: IO[(Out[A], Out[B])] = for {
      fibA <- ioa.start
      fibB <- iob.start
      resA <- fibA.join
      resB <- fibB.join
    } yield (resA, resB)

    res.flatMap {
      case (Succeeded(a), Succeeded(b)) => a.flatMap(aR => b.map(bR => (aR, bR)))
      case (Errored(e), _) => IO.raiseError(e)
      case _ => IO.raiseError(new RuntimeException("boom"))
    }
  }

  val ex2program = {
    val firstIO = IO.sleep(2.seconds) >> IO(1).debug
    val secondIO = IO.sleep(3.second) >> IO(2).debug

    ex2(firstIO, secondIO).debug.void
  }

  def timeout[A](io: IO[A], duration: FiniteDuration): IO[A] = {
    val computation: IO[Outcome[IO, Throwable, A]] = for {
      fib <- io.start
      _   <- IO.sleep(duration) >> fib.cancel
      res <- fib.join
    } yield res

    computation.flatMap {
      case Outcome.Succeeded(fa) => fa
      case Outcome.Errored(e) => IO.raiseError(e)
      case Outcome.Canceled() => IO.raiseError(new RuntimeException("Computation cancelled"))
    }
  }

  override def run = ex2program.void
}
