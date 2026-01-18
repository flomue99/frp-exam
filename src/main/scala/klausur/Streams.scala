package klausur


@main def main5(): Unit = {
  val numbers = List(2, -3, 9, 0, -1, 4, -3, 0)
  val posNeg = numbers
    .filter(n => n != 0)
    .groupBy(n => n > 0)

  val posNeg1 = numbers
    .filter(n => n != 0)
    .partitionMap(n => if (n > 0) Right(n) else Left(n))

  val optMax = numbers
    .map(n => Math.abs(n))
    .sortWith((a, b) => a > b)
    .headOption

  val sum = numbers.reduce((a, b) => a + b)

  println(posNeg)
  println(posNeg1)
  println(optMax)
  println(optMax.getOrElse("Not max found"))
  println(sum)
}