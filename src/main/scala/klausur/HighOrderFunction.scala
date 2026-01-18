package klausur


@main def main12(): Unit = {

  def reduce[E, R](lst: List[E], initial: R, acc: (R, E) => R): R = {
    lst match {
      case Nil => initial
      case head :: tail => reduce(tail, acc(initial, head), acc)
    }
  }

  val zahlen = List(1, 2, 3, 4)
  val summe = reduce(zahlen, 0, (sum, elem) => sum + elem)
  println(s"Summe: $summe") // Ausgabe: 10
}