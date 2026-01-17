package klausur

import scala.concurrent.Future
import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors, TimerScheduler}
import org.apache.pekko.actor.typed.{ActorRef, ActorSystem, Behavior, Signal, Terminated}

import scala.concurrent.Await
import scala.concurrent.duration.Duration


sealed trait FAdderCommand

final case class FCalculateSum(numbers: Seq[Int], replyTo: ActorRef[FCalculatorCommand]) extends FAdderCommand

sealed trait FCalculatorCommand

final case class FReceiveSum(sum: Int) extends FCalculatorCommand


object FunctionalAdder {

  def apply(): Behavior[FAdderCommand] =
    Behaviors.setup[FAdderCommand] { context =>

      Behaviors.receiveMessage[FAdderCommand] {
        case FCalculateSum(numbers: Seq[Int], replyTo: ActorRef[FCalculatorCommand]) => {
          val sum = numbers.foldLeft(0)((sum, acc) => sum + acc)
          replyTo ! FReceiveSum(sum)
          Behaviors.stopped
        }
      }
    }
}

object FunctionalCalculator {
  def apply(): Behavior[FCalculatorCommand] =
    Behaviors.setup[FCalculatorCommand] { context =>

      val adder1 = context.spawn(FunctionalAdder(), "adder-1")
      val adder2 = context.spawn(FunctionalAdder(), "adder-2")
      val adder3 = context.spawn(FunctionalAdder(), "adder-3")

      val s1 = Seq(1, 2, 3, 4, 5, 65, 6)
      val s2 = Seq(10, 30, 40, 100, 123, 213, 31, 3)
      val s3 = Seq(10, 30, 40, 100, 3, 4, 1, 2, 3, 14, 24, 124, 124, 5)

      context.children.foreach(context.watch)

      adder1 ! FCalculateSum(s1, context.self)
      adder2 ! FCalculateSum(s2, context.self)
      adder3 ! FCalculateSum(s3, context.self)

      Behaviors.receiveMessage[FCalculatorCommand] {
          case FReceiveSum(sum) => {
            println(s"Sum = $sum")
            Behaviors.same
          }
        }
        .receiveSignal {
          case (context, Terminated(actorRef)) => { // _context not needed here
            println(s"${actorRef.path.name} terminated")
            if (context.children.nonEmpty) {
              Behaviors.same
            } else {
              println("All actors terminated")
              Behaviors.stopped
            }
          }
        }
    }
}

@main def main1(): Unit = {

  val system = ActorSystem(FunctionalCalculator.apply(), "system")
  Thread.sleep(10000)
  Await.ready(system.whenTerminated, Duration.Inf)
}