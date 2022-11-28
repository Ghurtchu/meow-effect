package part2effects

import cats.effect.IO
import cats.effect.unsafe.implicits.global

object IOExercises {

  // ex1 - sequence two IO-s and take the result of the last one
  def takeRight[A, B](ioA: IO[A], ioB: IO[B]): IO[B] = ioA.flatMap(_ => ioB)

  // ex2 - sequence two IO-s and take the result of the first one
  def takeLeft[A, B](ioA: IO[A], ioB: IO[B]): IO[A] = ioA.flatMap(a => ioB.map(_ => a))

  // ex3 - run effect forever
  def forever[A](ioA: IO[A]): IO[A] = ioA.flatMap(_ => {
    val a = ioA.unsafeRunSync()
    println(a)
    forever(IO(a))
  })

  // ex4 - just map
  def convert[A, B](ioa: IO[A], b: B): IO[B] = ioa.map(_ => b)

  // ex5 - discard value just return unit
  def unit[A](ioa: IO[A]): IO[Unit] = convert(ioa, ())

  def main(args: Array[String]): Unit = {
    println(takeRight(IO.delay {
      println("left")
      100
    }, IO.delay {
      println("right")
      50
      }).unsafeRunSync())

    println(takeLeft(IO.delay {
      println("left")
      1000
    }, IO.delay {
      println("right")
      "boom"
    }).unsafeRunSync())

    println {
      forever {
        IO.delay {
          Thread sleep 125

          42
        }
      }.unsafeRunSync()
    }


  }

}
