package part2effects

import part2effects.Effects.IOMonad

object EffectsExercises {

  final case class IO[A](unsafeRun: () => A) {
    def map[B](f: A => B): IO[B] = new IO[B](() => f(unsafeRun()))
    def flatMap[B](f: A => IO[B]): IO[B] = new IO[B](() => f(unsafeRun()).unsafeRun())
  }

  object IO {
    def effect[A](a: => A): IO[A] = IO(() => a)
  }

  // 1) IO which returns current time of the system
  def clock: IO[Long] = IO.effect(System.currentTimeMillis())

  // 2) IO which measures the duration of computation
  def measure[A](computation: => A): IO[Long] = for {
    start <- clock
    _     <- IO.effect(computation)
    end   <- clock
  } yield end - start

  // 3 print something to the console
  def display(thing: String): IO[Unit] = IO.effect(println(thing))

  // 4 input
  def input: IO[String] = IO.effect(scala.io.StdIn.readLine)

  val cl = clock

  def main(args: Array[String]): Unit = {

    println {
      measure {
        Thread sleep 250
        val a = 5
        val b = a + 5
      }.unsafeRun()
    }

    display {
      "boom"
    }.unsafeRun()

    (for {
      a <- input
      b <- input
      _ <- display(a concat b)
    } yield ()).unsafeRun()



  }

}
