package klausur

import org.apache.pekko.protobufv3.internal.compiler.PluginProtos.CodeGeneratorResponse.Feature

import java.io.InvalidObjectException
import scala.collection.immutable.HashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}


@main def mondads(): Unit = {

  case class User(id: Int, name: String, email: String)


  val database = HashMap[Int, User](
    (1, User(1, "florian", "muehleder@mail.com")),
    (2, User(2, "jonas", "jonas@mail.com")),
    (3, User(3, "test", "mail.com")),
  )

  def fetchUserById(id: Int): Future[User] = {
    Future {
      // val user = database.get(id)
      // user.getOrElse(throw new NoSuchElementException(s"User with id {$id} not found"))
      database.getOrElse(id, throw new NoSuchElementException(s"User with id {$id} not found"))
    }
  }

  def validateUser(user: User): Try[User] = {
    if (user.name.nonEmpty && user.email.contains("@")) then Success(user)
    else Failure(throw new InvalidObjectException(s"Not a valid user "))
  }

  def validateUser1(user: User): Try[User] = user match {
    case user if user.name.nonEmpty && user.email.contains("@") => Success(user)
    case _ => Failure(throw new InvalidObjectException(s"Not a valid user "))
  }

  def validateUser2(user: User): Try[User] = Try {
    user match {
      case user if user.name.nonEmpty && user.email.contains("@") => user
      case _ => throw new InvalidObjectException(s"Not a valid user ")
    }
  }

  def fetchAndValidateUser(id: Int): Future[User] = {
    //  val x = fetchUserById(id).map {
    fetchUserById(id).flatMap {
      user =>
        validateUser2(user) match {
          case Success(user) => Future.successful(user)
          case Failure(exception) => Future.failed(exception)
        }

      //  x.flatten
    }
  }

  def printResult(id: Int): Unit = {
    fetchAndValidateUser(id).onComplete {
      case Failure(exception) => println(exception.toString)
      case Success(user) => println(user.toString)
    }
  }

  val userFuture = fetchUserById(2)
  val errorUserFuture = fetchUserById(10)

  userFuture.onComplete {
    case Failure(exception) => println(exception.toString)
    case Success(user) => println(user.toString)
  }

  fetchUserById(10).onComplete {
    case Failure(exception) => println(exception.toString)
    case Success(user) => println(user.toString)
  }

  fetchAndValidateUser(10).onComplete {
    case Failure(exception) => println(exception.toString)
    case Success(user) => println(user.toString)
  }

  fetchAndValidateUser(3).onComplete {
    case Failure(exception) => println(exception.toString)
    case Success(user) => println(user.toString)
  }

  printResult(12)
  printResult(1)

  Thread.sleep(1000)
}