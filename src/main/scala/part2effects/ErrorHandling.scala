package part2effects

import scala.util.{Failure, Success, Try}

object ErrorHandling {

  import cats.effect.IO
  import cats.effect.unsafe.implicits.global

  // IO: pure, delay, defer
  // create failed effects
  val failedEffect: IO[Int] = IO.delay[Int](throw new RuntimeException("boom")) // not a good approach

  val failedEffectBetter: IO[Int] = IO.raiseError[Int](new ArithmeticException("Division by zero"))

  // handle exceptions
  val dealtError: IO[Any] = failedEffectBetter.handleErrorWith {
    case _: RuntimeException => IO.delay(println("Safe"))
  }

  def int: Int = throw new RuntimeException("boom")

  val io: IO[Either[Throwable, Int]] = IO.delay[Int] {
    int
  }.attempt

  // turn into an Either
  val effectEither: IO[Either[Throwable, Int]] = failedEffectBetter.attempt

  // redeem: do the same in one go
  val redeemedEffect: IO[String] = failedEffectBetter.redeem(e => s"Failed: ${e.getMessage}", i => s"Succeeded: $i") // looks like a fold to me :)

  failedEffectBetter.redeemWith(
    _ => IO.delay(""),
    i => IO.delay(i.toString)
  )

  // redeemWith = same as redeem but with IO-s

  // exercises

  // 1 - construct potentially failed IO-s from standard data types, options/try/either
  def optionToIO[A](option: Option[A])(ifEmpty: Throwable): IO[A] = option match
    case Some(value) => IO.delay(value)
    case None => IO.raiseError(ifEmpty)

  def optionToIOFold[A](option: Option[A])(ifEmpty: Throwable): IO[A] = option.fold(IO.raiseError(ifEmpty))(IO.delay)

  def tryToIO[A](trya: Try[A]): IO[A] = trya match
    case Failure(exception) => IO raiseError exception
    case Success(value)     => IO delay value

  def eitherToIO[A](ei: Either[Throwable, A]): IO[A] = ei match
    case Left(value)  => IO raiseError value
    case Right(value) => IO delay value

  def handleIOError[A](io: IO[A])(handler: Throwable => A): IO[A] =
    io.redeem(handler, identity)

  def handleIOError2[A](io: IO[A])(handler: Throwable => IO[A]): IO[A] =
    io.redeemWith(handler, IO.pure)

  failedEffect.handleErrorWith(_ => IO.pure(5))


  def main(args: Array[String]): Unit = {

    println {
      optionToIOFold(Some(2))(new RuntimeException("boom")).unsafeRunSync()
    }

    // println(optionToIOFold(None)(new RuntimeException("boom")).unsafeRunSync())

    println(tryToIO(Try("asdsad".substring(0, 1000))))

    // failedEffectBetter.unsafeRunSync()

    println(eitherToIO(Right(5)))

    println(eitherToIO(Left(new RuntimeException("Boom"))))

  }

}
