package klausur.expr

sealed trait Expr

case class Lit(value: Double) extends Expr {
  override def toString: String = value.toString
}

case class Var(name: String) extends Expr {
  override def toString: String = name
}

sealed trait BinExpr(val left: Expr, val right: Expr) extends Expr

case class Add(l: Expr, r: Expr) extends BinExpr(l, r) {
  override def toString: String = s"( $l + $r )"
}

case class Mult(l: Expr, r: Expr) extends BinExpr(l, r) {
  override def toString: String = s"( $l * $r )"
}

sealed trait UnExpr(val sub: Expr) extends Expr

case class Neg(s: Expr) extends UnExpr(s) {
  override def toString: String = s"( -$s )"
}

case class Recip(s: Expr) extends UnExpr(s) {
  override def toString: String = s"( 1 / $s )"
}


def infix(expr: Expr): String = {
  expr match {
    case Lit(value) => value.toString
    case Var(name) => name
    case Add(left, right) => s"( ${infix(left)} + ${infix(right)} )"
    case Mult(left, right) => s"( ${infix(left)} * ${infix(right)} )"
    case Neg(s) => s"( -${infix(s)} )"
    case Recip(s) => s"( 1 / ${infix(s)} )"
  }
}

def eval(expr: Expr, bds: Map[String, Double]): Double = {
  def eval(expr: Expr): Double = {
    expr match {
      case Lit(v) => v
      case Var(n) => bds(n)
      case Add(l, r) => eval(l) + eval(r)
      case Mult(l, r) => eval(l) * eval(r)
      case Neg(s) => -eval(s)
      case Recip(s) => 1 / eval(s)
    }
  }

  eval(expr)
}

def simplify(expr: Expr): Expr = ???
  
