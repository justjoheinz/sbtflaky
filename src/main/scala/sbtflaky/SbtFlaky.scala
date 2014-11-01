package sbtflaky

import scala.sys.process._
import scalaz._
//import scalaz.IndexedReaderWriterState._
//import scalaz.Id._
//import scalaz.std.vector._
//import scalaz.std.string._
//import scalaz.syntax.std.string._
//import scalaz.syntax.foldable._
//import scalaz.syntax.show._
import Scalaz._
import Console._
import Runner._
import Stats._

object SbtFlaky extends App {
  val StatsState = IndexedReaderWriterState.rwstMonad[Id, Unit, Vector[String], Stats]
  import StatsState._

  val parser = new scopt.OptionParser[CommandLineConfig]("sbtflaky") {
    head("sbtflaky", "0.0.1")
    opt[Int]('n', "numberRuns") required () action { (cNrRuns, c) =>
      c.copy(nrRuns = cNrRuns)
    } validate { cNrRuns =>
      if (cNrRuns > 0) success else failure("numberRuns must be greater zero")
    } text ("execute the test a specified number of times")
    arg[String]("<sbt test command>") required () unbounded () action { (cCmd, c) =>
      c.copy(cmd = c.cmd :+ cCmd)
    } text ("the sbt command to execute")
    help("help") text ("prints this usage text")
    note("""|
            |Examples:
            |  sbtflaky -n 10 testOnly MySpec
            |  sbtflaky -n 3 testOnly *DBSpec
            | 
            |The output of all failed command will be piped to stdout.""".stripMargin)
  }

  parser.parse(args, CommandLineConfig()) map { config =>
    val initialReader = ()
    val initialState = Stats(maxRuns = config.nrRuns)
    val (log, _, _) =
      whileM_(gets(_.maxRuns >= 0), executeTest(config.cmd.mkString(" "))).run(initialReader, initialState)
    log match {
      case logOut @ _ +: _ =>
        println(s"${RED}Error Log:$RESET")
        println(logOut.foldMap(s => s + "\n"))
      case _ =>
        println(s"${GREEN}No errors$RESET")
    }
  }
}

case class CommandLineConfig(nrRuns: Int = 10, cmd: List[String] = Nil)

object CommandLineConfig {
  implicit def cmdLineConfigShows: Show[CommandLineConfig] = new Show[CommandLineConfig] {
    override def show(c: CommandLineConfig): Cord = s"(${c.nrRuns}, ${c.cmd.foldLeft("") { case (r, e) => r + " " + e }}"
  }
}

object Runner {

  def executeTest(testCmd: String): ReaderWriterState[Unit, Vector[String], Stats, Unit] = ReaderWriterState {
    case (w, stats) =>
      import stats._
      println(stats.show)
      var output = Vector[String]()
      val processLogger = ProcessLogger(line => output = output :+ line)
      val exitCode = Process("sbt", List(testCmd)).run(processLogger).exitValue()
      if (exitCode == 0) {
        val (newStats, _) = _maxOk.run(stats)
        (Vector[String](), (), newStats)
      } else {
        val (newStats, _) = _maxFailed.run(stats)
        val header = s"""|$BLUE
                         |Log for build : $RED${maxRuns - failedRuns - okRuns}
                         |$RESET
                       """.stripMargin
        (header +: output, (), newStats)
      }
  }
}

case class Stats(maxRuns: Int, failedRuns: Int = 0, okRuns: Int = 0)

object Stats {
  val _maxRuns = Lens.lensu[Stats, Int]((stats, mr) => stats.copy(maxRuns = mr), _.maxRuns)
  val _failedRuns = Lens.lensu[Stats, Int]((stats, fr) => stats.copy(failedRuns = fr), _.failedRuns)
  val _okRuns = Lens.lensu[Stats, Int]((stats, ok) => stats.copy(okRuns = ok), _.okRuns)
  val _maxOk = for {
    _ <- _maxRuns %= { _ - 1 }
    _ <- _okRuns %= { _ + 1 }
  } yield ()
  val _maxFailed = for {
    _ <- _maxRuns %= { _ - 1 }
    _ <- _failedRuns %= { _ + 1 }
  } yield ()

  implicit val statsShow: Show[Stats] = Show.shows { stats =>
    f"Remaining runs: ${BLACK}${stats.maxRuns}${RESET} " +
      f"failedRuns: ${RED}${stats.failedRuns}${RESET} " +
      f"okRuns: ${GREEN}${stats.okRuns}${RESET}"
  }
}