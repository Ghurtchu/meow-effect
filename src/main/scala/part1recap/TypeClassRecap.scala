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
    trait MyApplicative[F[_]] extends functor.MyFunctor[F] {
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

  object flatmap {
    trait FlatMap[F[_]] {
      def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
    }

    object FlatMap {
      def apply[F[_]](implicit flatMap: FlatMap[F]): FlatMap[F] = flatMap

      given listFlatmap: FlatMap[List] with {
        override def flatMap[A, B](fa: List[A])(f: A => List[B]): List[B] = fa.flatMap(f)
      }

      object FlatMapSyntax {
        extension[F[_]: FlatMap, A](self: F[A])
          def flatMap[B](f: A => F[B]): F[B] = FlatMap[F].flatMap(self)(f)
      }

    }

    import flatmap.FlatMap.FlatMapSyntax._

    def flatMap[F[_]: FlatMap, A, B](fa: F[A])(f: A => F[B]): F[B] = fa.flatMap(f)

  }

  object cats_flatmap {

    import cats.FlatMap
    import cats.syntax.flatMap._
    import cats.syntax.functor._

    def flatMap[F[_]: FlatMap, A, B](fa: F[A])(f: A => F[B]): F[B] = fa flatMap f

    def crossProduct[F[_]: FlatMap, A, B](fa: F[A], fb: F[B]): F[(A, B)] = for {
      a <- fa
      b <- fb // map syntax from functor
    } yield (a, b)

    val res = flatMap[List, Int, String](1 :: 2 :: Nil)(n => List(s"$n", s"${n + 1}"))

  }

  object monad {

    import applicative.MyApplicative
    import flatmap.FlatMap

    trait MyMonad[F[_]] extends MyApplicative[F] with FlatMap[F] {
      override def map[A, B](fa: F[A])(f: A => B): F[B] = flatMap(fa)(f andThen pure)
    }
  }

  object cats_monad {

    import cats.Monad
    import cats.syntax.flatMap._
    import cats.syntax.functor._

    def perform[F[_]: Monad, A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = for {
      a <- fa
      b <- fb
    } yield f(a, b)

    val res = perform[Option, Int, String, Boolean](Option(1), Option("1"))(_ == _.toInt)
  }

  object applicative_error {
    import applicative._

    trait MyApplicativeError[F[_], E] extends MyApplicative[F]{
      def raiseError[A](e: E): F[A] // build instances of F[A]
    }

  }

  object cats_applicative_error {

    import cats.ApplicativeError

    type ErrorOr[A] = Either[String, A]

    val failed: ErrorOr[Int] = ApplicativeError[ErrorOr, String].raiseError("Boom")
    val successful: ErrorOr[Int] = ApplicativeError[ErrorOr, String].pure[Int](42)

  }

  object traverse {

    trait Traverse[F[_]] extends functor.MyFunctor[F] {

    }

  }

  object cats_traverse {
    import cats.Traverse
    val listTraverse = Traverse[List]
    val data: List[Option[Int]] = Some(1) :: Some(2) :: None :: Nil
    val optionList: Option[List[String]] = listTraverse.traverse(1 :: 2 :: 3 :: Nil)(num => Option(num.toString))

    val seq: Option[List[Int]] = listTraverse.sequence(data) // love this <3

  }


  def main(args: Array[String]): Unit = {

  }
}
