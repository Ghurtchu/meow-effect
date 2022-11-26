package part1recap

object TypeClassRecap {

  /**
   * - Applicative
   * - Functor
   * - FlatMap
   * - Monad
   * - Apply
   * - ApplicativeError / MonadError
   * - Traverse
   */

  // data type that is mappable
  object functor {

    // definition
    trait MyFunctor[F[_]] {
      def map[A, B](fa: F[A])(f: A => B): F[B]
    }

    object MyFunctor {
      def apply[F[_]](implicit functor: MyFunctor[F]): MyFunctor[F] = functor

      given listFunctor: MyFunctor[List] with
        override def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)

      given optionFunctor: MyFunctor[Option] with
        override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)

      object MyFunctorSyntax {
        extension[F[_]: MyFunctor, A](self: F[A])
          def map[B](f: A => B): F[B] = MyFunctor[F].map(self)(f)
      }
    }

    import MyFunctor.*
    import MyFunctor.MyFunctorSyntax.*

    def map[F[_]: MyFunctor, A, B](fa: F[A])(f: A => B): F[B] = fa.map(f)

    val result1 = map[Option, Int, String](Option(5))(_.toString)
    val result2 = map[List, String, Boolean]("123" :: "345" :: "333" :: Nil)(_.contains("3"))

  }

  object cats_functor {

    import cats.Functor
    import cats.instances.option._
    import cats.instances.list._
    import cats.instances.int._
    import cats.instances.string._

    import cats.syntax.functor._

    def map[F[_]: Functor, A, B](fa: F[A])(f: A => B): F[B] = fa map f

    map[Option, Int, String](Option(5))(_.toString)
    map[List, Int, Int](1 :: 2 :: 3 :: Nil)(_ + 1)

  }

  object applicative {
    trait MyApplicative[F[_]] {
      def pure[A](value: A): F[A] // lifts value into some kind of container (Try, Future, List, Option, Either etc..)
    }
  }

  object cats_applicative {
    import cats.Applicative
    import cats.syntax.applicative._

    val listApplicative: Applicative[List] = Applicative[List]
    val liftedInt: List[Int] = listApplicative.pure(44)

    // extension methods
    val oneOption: Option[Int] = 1.pure[Option]
    val oneList: List[Int]     = 42.pure[List]
  }

  def main(args: Array[String]): Unit = {


  }

}
