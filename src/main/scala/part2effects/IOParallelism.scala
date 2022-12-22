package part2effects

import cats.effect.{IO, IOApp}
import utils._

// effects can run in multiple threads
object IOParallelism extends IOApp.Simple {

  // IOs are usually sequential

  val anisIO = IO(println(s"[${Thread.currentThread().getName}] Ani"))
  val kamranIO = IO(println(s"[${Thread.currentThread().getName}] Kamran"))

  val program: IO[String] = for {
    ani <- anisIO
    kamran <- kamranIO
  } yield s"$ani and $kamran love RTJVM"


  val mol: IO[Int] = IO.delay(42).debug
  val favLang: IO[String] = IO.delay("Scala").debug

  import cats.syntax.apply._

  val prog = (mol, favLang).mapN { (num, txt) =>
    s"my goal in life is $num and $txt"
  }

  import cats.Parallel

  val parIO1: IO.Par[Int] = Parallel[IO].parallel(mol.debug)
  val parIO2: IO.Par[String] = Parallel[IO].parallel(favLang.debug)

  import cats.effect.implicits._

  val progPar: IO.Par[String] = (parIO1, parIO2).mapN { (n, s) =>
    s"my goal in life is $n and $s"
  }

  val goalInLife: IO[String] = Parallel[IO].sequential(progPar)

  // shorthand
  import cats.syntax.parallel._
  val goalInLifeV3: IO[String] = (mol, favLang).parMapN { (n, s) =>
    s"my goal in life is $n and $s"
  }

  // failure
  val aFailedIO: IO[String] = IO.raiseError(new RuntimeException("boom"))
  // compose success + failure
  val parallelWithFailure: IO[String] = (mol, aFailedIO.debug).parMapN { (i, s) =>
    i.toString concat s
  }.redeem(_ => "xD", identity)

  // composed
  val ex: IO[String] = IO.raiseError(new RuntimeException("f1"))

  val comp = (aFailedIO.debug, ex.debug).parMapN(_ concat _)

  override def run: IO[Unit] = comp.debug.void


}
