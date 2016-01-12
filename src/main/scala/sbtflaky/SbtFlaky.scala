package sbtflaky

import sbtflaky.ColorHelper._
import sbtflaky.ExitCode._
import sbtflaky.Stats._
import sbtflaky.CommandLineParser._

import scala.Console._
import scala.sys.process._
import scalaz._
import Scalaz._


object SbtFlaky extends App with SbtTestRunner {

  for {
    config <- parser.parse(args, CommandLineConfig())
  } yield {
    config |> (startTestRuns _ andThen (printResult _).tupled)
  }

}

trait SbtTestRunner {

  val StatsState = ReaderWriterStateT.rwstMonad[Id, String, Log, Stats]

  import StatsState._

  import scala.collection.mutable.ArrayBuffer

  def startTestRuns(config: CommandLineConfig): (Log, ExitCode) = {
    val initialReader = config.fullCommand
    val initialState = Stats(maxRuns = config.nrRuns)
    val (log, exitCodes, _) = whileM[List, ExitCode](gets(_.maxRuns > 0), executeTest(config.fullCommand)).run(initialReader, initialState)
    (log, exitCodes.suml)
  }

  def printResult(log: Log, exitCode: ExitCode): Unit = {
    import sbtflaky.ColorHelper._
    exitCode match {
      case FailedTest =>
        println(colored(RED, "Error Log:"))
        println(s"Log : $log")
        println(log.foldMap(s => s + "\n"))
      case Ok =>
        println(colored(GREEN, "No errors"))
    }
  }


  private def executeTest(cmd: String): SbtRun[ExitCode] = {

    def failed(output: ArrayBuffer[String]): SbtRun[Unit] = {
      for {
        _ <- _increaseFailedRuns.rwst[Log, String]
        state <- get
        header = colored(BLUE, s"""Log for build : $RED${state.maxRuns - state.failedRuns - state.okRuns}\n""")
        _ <- tell((header +: output).toVector)
      } yield ()
    }

    val (output, processLogger) = createProcessLogger()
    val result: SbtRun[ExitCode] = for {
      _ <- gets(stats => println(stats.show))
      exitCode <- gets(stats => Process("sbt", List(cmd)).run(processLogger).exitValue().asExitCode)
      _ <- exitCode match {
        case Ok =>
          _increaseSuccessfulRuns.rwst[Log, String]
        case FailedTest =>
          failed(output)
      }
    } yield exitCode

    result
  }

  private def createProcessLogger(): (ArrayBuffer[String], ProcessLogger) = {
    val output = collection.mutable.ArrayBuffer[String]()
    val processLogger = ProcessLogger(line => output += line)
    (output, processLogger)
  }
}
