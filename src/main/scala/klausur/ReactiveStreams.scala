package klausur

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.stream.scaladsl.Sink
import org.apache.pekko.stream.scaladsl.Source

object ReactiveStreams {
  //
  //  def transform(numbers: Seq[Int], f: Int => Int): Future[Seq[Int]] = Future {
  //    // numbers.map(n => f(n))
  //    numbers.map(f)
  //  }
  //
  //  def sumSquares(numbers: Seq[Int]): Future[Int] = {
  //    val squaredNumbersSeq: Future[Seq[Int]] = transform(numbers, (n => n * n))
  //    squaredNumbersSeq.map { numbers => numbers.fold(0)((sum, acc) => sum + acc)
  //    }
  //  }

  def transform(numbers: Seq[Int], f: Int => Int)(using system: ActorSystem[_]): Future[Seq[Int]] = {
    Source(numbers)
      .map(n => f(n))
      .runWith(Sink.seq)
  }

  def sumSquares(numbers: Seq[Int])(using system: ActorSystem[_]): Future[Int] = {
    val squaredNumbersSeq: Future[Seq[Int]] = transform(numbers, (n => n * n))

    Source.future(squaredNumbersSeq)
      .map(numbers => numbers.fold(0)((sum, acc) => sum + acc))
      .runWith(Sink.head)
  }
}

@main def main3(): Unit = {

  given system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "MyReactiveSystem")
  val numbers = Seq(1, 2, 3)

  val future = ReactiveStreams.sumSquares(numbers)

  future.onComplete {
    case Failure(exception) => println(exception.getMessage)
    case Success(sum) => println(s"sum= $sum")
  }

  Await.ready(future, Duration.Inf)

}