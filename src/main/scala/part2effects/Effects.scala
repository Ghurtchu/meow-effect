package part2effects

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Effects {

  // pure functional programming
  // substitution => referential transparency

  def combine(a: Int, b: Int): Int = a + b
  // evaluation steps
  val five: Int = combine(2, 3) // step 1
  val five_v2: Int = 2 + 3      // step 2
  val five_v3: Int = 5          // step 3

  // example of side effect: print to the console
  val printSomething: Unit = println("Cats effect")
  val printSomething_v2: Unit = () // line 16 and line 15 are not the same, so this breaks RT thereby this is impure

  // example: change the variable
  var anInt = 0
  val value: Unit = (anInt += 1) // side effect
  val value_2: Unit = () // breaks RT

  // side effects are inevitable

  // effects (cats IO, ZIO etc)

  /**
   * Desires:
   * - type signature describes what kind of computation will be performed if we evaluate it
   * - what kind of value will be produced by applying that calculation
   * - when side effects are needed, effect construction is separate of effect execution
   */

  // Option
  // 1 - checks (describes potentially absent value)
  // 2 - checks (we know that it's either Some(Int) or None)
  // 3 - checks (side effects are not needed)
  val anOption: Option[Int] = Option(42)

  // Future
  // 1 - checks (describes async computation)
  // 2 - checks (either Success[Int] or Failure[Throwable])
  // 3 - does not check (allocating or scheduling thread pool is required to run Future) Execution is not separate from construction
  import scala.concurrent.ExecutionContext.Implicits.global
  val aFuture: Future[Int] = Future(42)

  // our IO data type
  // and it's a fair-like-square effect :)
  final case class IOMonad[A](unsafeRun: () => A) {
    def map[B](f: A => B): IOMonad[B] = new IOMonad[B](() => f(unsafeRun()))
    def flatMap[B](f: A => IOMonad[B]): IOMonad[B] = new IOMonad[B](() => f(unsafeRun()).unsafeRun())
  }

  val printIO: IOMonad[Unit] = for {
    _ <- IOMonad(() => println("Hello there"))
    _ <- IOMonad(() => println("Hello there again.."))
  } yield ()
  
  def main(args: Array[String]): Unit = {
    printIO.unsafeRun() // only place where side effect is produced
  }

}
