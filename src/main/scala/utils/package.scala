import cats.effect.IO

package object utils {

  implicit class IOOps[A](self: IO[A]) {

    def debug: IO[A] = for {
      a <- self
      t = Thread.currentThread().getName
      _ = println(s"[$t] $a")
    } yield a

  }

}
