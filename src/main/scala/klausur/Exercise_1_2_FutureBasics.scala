package klausur

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Parallel {
  def doInParallel(codeBlockA: => Unit, codeBlockB: => Unit): Future[Unit] = {
    val future1 = Future {
      codeBlockA
    }
    val future2 = Future {
      codeBlockB
    }

    //future1.zip(future2).map(_ => ())
    //Future.sequence(Seq(future1, future2)).map(_ => ())

    val res: Future[Unit] = for {
      _ <- future2
      _ <- future1
    } yield ()

    res
  }

  def doInParallel[U, V](f1: Future[U], f2: Future[V]): Future[(U, V)] = {
    //solution 1)
    //  val res =   for {
    //      r1 <- f1
    //      r2 <- f2
    //    } yield (r1, r2)
    // res

    //solution 2)
    //    val res = f2.flatMap {
    //      res2 =>
    //        f1.map {
    //          res1 => (res1, res2)
    //        }
    //    }
    //    res
    //solution 3)
    f1.zip(f2)
  }
}

@main def hello(): Unit = {

  import Parallel._


  //  val successResult = doInParallel(
  //    {
  //      println("[1] started")
  //      Thread.sleep(1000)
  //      println("[1] completed")
  //    },
  //    {
  //      println("[2] started")
  //      Thread.sleep(3000)
  //      println("[2] completed")
  //    }
  //  )

  //  val failureResult = doInParallel(
  //    {
  //      println("[1] started")
  //      Thread.sleep(1000)
  //      throw new ArithmeticException("Failed")
  //      println("[1] completed")
  //    },
  //    {
  //      println("[2] started")
  //      Thread.sleep(6000)
  //      println("[2] completed")
  //    }
  //  )

  //  successResult.onComplete {
  //    case Success(_) => println("Both computations finished successfully!")
  //    case Failure(ex) => println(s"An error occurred: ${ex.getMessage}")
  //  }

  //  failureResult.onComplete {
  //    case Success(_) => println("Both computations finished successfully!")
  //    case Failure(ex) => println(s"An error occurred: ${ex.getMessage}")
  //  }


  val successResult = doInParallel(
    Future {
      println("[1] started")
      Thread.sleep(1000)
      println("[1] completed")
    },
    Future {
      println("[2] started")
      Thread.sleep(3000)
      println("[2] completed")
    }
  )

  successResult.onComplete {
    case Success(_) => println("Both computations finished successfully!")
    case Failure(ex) => println(s"An error occurred: ${ex.getMessage}")
  }

  Thread.sleep(10000)
}