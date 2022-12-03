package part2effects

object ErrorHandling {

  import cats.effect.IO
  import cats.effect.unsafe.implicits.global

  // IO: pure, delay, defer
  // create failed effects
  val failedEffect = IO.delay[Int](throw new RuntimeException("boom")) // not a good approach

  val failedEffectBetter = IO.raiseError[Int](new ArithmeticException("Division by zero"))


  // handle exceptions
  val dealtError: IO[Any] = failedEffectBetter.handleErrorWith {
    case _: RuntimeException => IO.delay(println("Safe"))
  }

  // turn into an Either
  val effectEither: IO[Either[Throwable, Int]] = failedEffectBetter.attempt

  // redeem: do the same in one go
  val redeemedEffect = failedEffectBetter.redeem(e => s"Failed: ${e.getMessage}", i => s"Succeeded: $i")

  // redeemWith = same as redeem but with IO-s

  def main(args: Array[String]): Unit = {

    println(dealtError.unsafeRunSync())
    println(redeemedEffect.unsafeRunSync())

    // failedEffectBetter.unsafeRunSync()

  }

}
