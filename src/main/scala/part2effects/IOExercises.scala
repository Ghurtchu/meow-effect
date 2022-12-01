package part2effects

import cats.effect.IO
import cats.effect.unsafe.implicits.global

object IOExercises {

  // ex1 - sequence two IO-s and take the result of the last one
  def takeRight[A, B](ioA: IO[A], ioB: IO[B]): IO[B] = ioA.flatMap(_ => ioB)
  def takeRight_v2[A, B](ioA: IO[A], ioB: IO[B]): IO[B] = ioA *> ioB // it's like ZIO.zipRight or something
  def takeRight_v3[A, B](ioA: IO[A], ioB: IO[B]): IO[B] = ioA >> ioB // takes a by name param (unevaluated IO)

  // ex2 - sequence two IO-s and take the result of the first one
  def takeLeft[A, B](ioA: IO[A], ioB: IO[B]): IO[A] = ioA.flatMap(a => ioB.map(_ => a))
  def takeLeft_v2[A, B](ioA: IO[A], ioB: IO[B]): IO[A] = ioA <* ioB // kinda zipLeft thingy

  // ex3 - run effect forever
  def forever[A](ioA: IO[A]): IO[A] = ioA.flatMap(_ => forever(ioA))
  def forever_v2[A](ioA: IO[A]): IO[A] = ioA >> forever_v2(ioA) // stack-safe
  def forever_v3[A](ioA: IO[A]): IO[A] = ioA.foreverM // cats API

  // ex4 - just map
  def convert[A, B](ioa: IO[A], b: B): IO[B] = ioa.map(_ => b)
  def convert_v2[A, B](ioa: IO[A], b: B): IO[B] = ioa as b // cats API

  // ex5 - discard value just return unit
  def unit[A](ioa: IO[A]): IO[Unit] = convert(ioa, ())
  def unit_v2[A](ioa: IO[A]): IO[Unit] = ioa.map(_ => ())
  def unit_v3[A](ioa: IO[A]): IO[Unit] = ioa as () // cats API
  def unit_v4[A](ioa: IO[A]): IO[Unit] = ioa.void // cats API

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
