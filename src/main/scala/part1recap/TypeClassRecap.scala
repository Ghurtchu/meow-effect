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
    }

    import MyFunctor.*

    def map[F[_]: MyFunctor, A, B](fa: F[A])(f: A => B): F[B] = MyFunctor[F].map(fa)(f)

    val result1 = map[Option, Int, String](Option(5))(_.toString)
    val result2 = map[List, String, Boolean]("123" :: "345" :: "333" :: Nil)(_.contains("3"))

  }

  def main(args: Array[String]): Unit = {

  }

}
