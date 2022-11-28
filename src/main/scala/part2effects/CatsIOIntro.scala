package part2effects

object CatsIOIntro {

  import cats.effect.IO

  // IO
  val firstIO: IO[Int] = IO.pure[Int](42) // lifts Int into IO[Int] this is not lazy, it's eagerly evaluated like ojb.apply[A](a: A): otherobj[A] = otherobj(a)

  // side-effecting delayed computation
  val delayedIO: IO[Int] = IO.delay[Int] {
    Thread sleep 125

    42
  }

  val delayedIO2: IO[String] = IO("Hey there") // sugar syntax for IO.apply

}
