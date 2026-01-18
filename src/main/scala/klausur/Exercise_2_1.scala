package klausur

import scala.concurrent.Future
import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors, TimerScheduler}
import org.apache.pekko.actor.typed.{ActorRef, ActorSystem, Behavior, Signal, Terminated}

import scala.concurrent.Await
import scala.concurrent.duration.Duration


case class Message(msg: String, id: Int)

sealed trait Command

case class SendMessage(msg: Message, replyTo: ActorRef[Reply]) extends Command

sealed trait Reply

case class ConfirmMessage(msgId: Int) extends Reply


object MessageSender {

  def apply(): Behavior[Reply] = {
    Behaviors.setup { context =>
      val receiver1 = context.spawn(MessageReceiver(), "receiver-1")
      val receiver2 = context.spawn(MessageReceiver(), "receiver-2")
      val receiver3 = context.spawn(MessageReceiver(), "receiver-3")

      val msg1 = Message("Test1", 1)
      val msg2 = Message("Test2", 2)
      val msg3 = Message("Test3", 3)

      receiver1 ! SendMessage(msg1, context.self)
      receiver2 ! SendMessage(msg2, context.self)
      receiver3 ! SendMessage(msg3, context.self)

      context.watch(receiver1)
      context.watch(receiver2)
      context.watch(receiver3)

      Behaviors.receiveMessage[Reply] {
        case ConfirmMessage(id: Int) => {
          println(s"Message with id{$id} confirmed")
          Behaviors.same
        }
      }.receiveSignal {
        case (context, Terminated(actorRef)) => {
          if context.children.nonEmpty then Behaviors.same else {
            println("Sender terminated")
            Behaviors.stopped
          }
        }
      }
    }

  }
}

object MessageReceiver {

  def apply(): Behavior[Command] = {
    Behaviors.setup[Command] { context => {
      Behaviors.receiveMessage[Command] {
        case SendMessage(msg: Message, replyTo: ActorRef[Reply]) => {

          println(s"Message received: msg={${msg.msg}}, id={$msg.id")
          replyTo ! ConfirmMessage(msg.id)
          Behaviors.stopped
        }
      }
    }
    }
  }
}

@main def mayMain(): Unit = {

  val system = ActorSystem(MessageSender(), "system")
  Await.ready(system.whenTerminated, Duration.Inf)

}