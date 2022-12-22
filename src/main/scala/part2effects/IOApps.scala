package part2effects

import cats.effect.{ExitCode, IO, IOApp}

import scala.io.StdIn

object IOApps {

}

object TestApp {
  import cats.effect._
  import IOApps._

  val program: IO[Unit] = for {
    line <- IO(StdIn.readLine())
    _    <- IO(println(line))
  } yield ()


  def main(args: Array[String]): Unit = {

    import cats.effect.unsafe.implicits.global
    program.unsafeRunSync()
  }
}

object FirstCatsEffectApp extends IOApp {

  import TestApp.program

  override def run(args: List[String]): IO[ExitCode] = {
    program.as(ExitCode.Success)
    // program.map(_ => ExitCode.Success)
  }

}
