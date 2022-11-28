package part2effects

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

  def main(args: Array[String]): Unit = {

  }

}
