package klausur.expr

object ExprApp {

  def main(args: Array[String]): Unit = {
    println("Hello World")
    val e1 = Add(Lit(1), Mult(Var("x"), Lit(3)))
    println(e1.toString)
    println(infix(e1))
  }

}
