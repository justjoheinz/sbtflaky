package sbtflaky

import scala.sys.process._
import scalaz._
import Scalaz._
import Console._
import Runner._
import Stats._

object SbtFlaky extends App {
  val StatsState = IndexedReaderWriterState.rwstMonad[Id, Unit, DList[String], Stats]
  import StatsState._

  args match {
    case Array(max, testOnly, test, _*) if (max.parseInt.isSuccess) =>
      val (log, _, _) = whileM_(gets(_.maxRuns >= 0), executeTest(testOnly + " " + test)).run((), Stats(maxRuns = max.toInt))
      log.toList match {
        case Nil =>
          println(s"${GREEN}No errors$RESET")
        case logOut =>
          println(s"${RED}Error Log:$RESET")
          println(logOut.foldMap(s => s + "\n"))
      }
      println(s"$RED Error Log:$RESET")
      println(log.foldMap(s => s + "\n"))
    case _ => println("""|sbtflaky <max> <test command>""".stripMargin)
  }
}

object Runner {

  def executeTest(testCmd: String): ReaderWriterState[Unit, DList[String], Stats, Unit] = ReaderWriterState {
    case (w, stats) =>
      import stats._
      println(stats.show)
      var output = DList[String]()
      val processLogger = ProcessLogger(line => output = output :+ line)
      val exitCode = Process("sbt", List(testCmd)).run(processLogger).exitValue()
      if (exitCode == 0) {
        val (newStats, _) = _maxOk.run(stats)
        (DList[String](), (), newStats)
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