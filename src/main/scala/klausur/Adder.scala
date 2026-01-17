package klausur

import scala.concurrent.Future
import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors, TimerScheduler}
import org.apache.pekko.actor.typed.{ActorRef, ActorSystem, Behavior, Signal, Terminated}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

sealed trait AdderCommand

final case class CalculateSum(numbers: Seq[Int], replyTo: ActorRef[CalculatorCommand]) extends AdderCommand

sealed trait CalculatorCommand

final case class StartCalculation() extends CalculatorCommand

final case class ReceiveSum(sum: Int) extends CalculatorCommand

private object Adder:

  def apply(): Behavior[AdderCommand] =
    Behaviors.setup(context => new Adder(context))

private class Adder(context: ActorContext[AdderCommand]) extends AbstractBehavior[AdderCommand](context):

  override def onMessage(msg: AdderCommand): Behavior[AdderCommand] = msg match {
    case CalculateSum(numbers, replyTo: ActorRef[CalculatorCommand]) => {
      //val sum = numbers.sum

      val sum = numbers.foldLeft(0)((a, b) => a + b) // or numbers.sum
      replyTo ! ReceiveSum(sum)
      Behaviors.stopped
    }
  }

private object Calculator:
  def apply(): Behavior[CalculatorCommand] =
    Behaviors.setup(context => new Calculator(context))

private class Calculator(context: ActorContext[CalculatorCommand]) extends AbstractBehavior[CalculatorCommand](context):

  private var activeCalculations = 0

  override def onMessage(msg: CalculatorCommand): Behavior[CalculatorCommand] = msg match {
    case StartCalculation() => {
      val numbers1 = Seq(1, 2, 3, 4, 5, 6)
      val numbers2 = Seq(10, 20, 30)

      val adder1 = context.spawn(Adder(), "adder-1")
      val adder2 = context.spawn(Adder(), "adder-2")

      adder1 ! CalculateSum(numbers1, context.self)
      adder2 ! CalculateSum(numbers2, context.self)

      context.watch(adder1)
      context.watch(adder2)

      activeCalculations = 2

      Behaviors.same
    }

    case ReceiveSum(sum) => {
      println(s"sum= $sum")
      Behaviors.same
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[CalculatorCommand]] = {
    case Terminated(actorRef) =>
      activeCalculations -= 1
      println(s"Aktor ${actorRef.path.name} ist beendet. Verbleibend: $activeCalculations")

      if (activeCalculations == 0) {
        println("Alle Berechnungen abgeschlossen. Calculator beendet sich.")
        Behaviors.stopped
      } else {
        Behaviors.same
      }
  }

@main def main(): Unit = {
  val mainBehavior: Behavior[Unit] = Behaviors.setup {
    context =>
      val calculator = context.spawn(Calculator(), "calculator")
      calculator ! StartCalculation()

      Behaviors.same
  }
  val system: ActorSystem[Unit] = ActorSystem(mainBehavior, "actorSystem")

  Thread.sleep(10000)
  system.terminate()
  Await.ready(system.whenTerminated, Duration.Inf)
}
