package sbtflaky

import scalaz.Monoid

sealed trait ExitCode
case object Ok extends ExitCode
case object FailedTest extends ExitCode


object ExitCode {
  implicit class Int2ExitCode(exitCode: Int) {
    def asExitCode : ExitCode = exitCode match {
      case 0 => Ok  
      case _ => FailedTest
    }
  }
  
  implicit val exitCodeMonoid = new Monoid[ExitCode] {
    def append(e1: ExitCode, e2: => ExitCode): ExitCode = {
      (e1, e2) match {
        case (Ok, Ok) => Ok : ExitCode
        case _        => FailedTest : ExitCode
      }
    }

    val zero = Ok

  }
}
