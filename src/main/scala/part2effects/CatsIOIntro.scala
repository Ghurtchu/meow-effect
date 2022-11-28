package part2effects

// IO Runtime
import cats.effect.unsafe.implicits.global

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

  val mol = IO(42)
  val mappedMol = mol.map(_ + 5)
  val mappedMolPrinted = mappedMol.flatMap(n => IO(println(n)))

  import cats.syntax.apply._
  val summed = (mol, mol).mapN { (a, b) =>
    a + b
  }

  def main(args: Array[String]): Unit = {
    mappedMolPrinted.unsafeRunSync()
    println(summed.unsafeRunSync())
  }

}
