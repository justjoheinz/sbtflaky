package sbtflaky

import scalaz._
import Scalaz._
import scala.io.AnsiColor._

case class Stats(maxRuns: Int, failedRuns: Int = 0, okRuns: Int = 0)

object Stats {
  // Some lenses
  val _maxRuns = Lens.lensu[Stats, Int]((stats, mr) => stats.copy(maxRuns = mr), _.maxRuns)

  val _failedRuns = Lens.lensu[Stats, Int]((stats, fr) => stats.copy(failedRuns = fr), _.failedRuns)

  val _okRuns = Lens.lensu[Stats, Int]((stats, ok) => stats.copy(okRuns = ok), _.okRuns)

  // Some stateful calculations
  val _increaseSuccessfulRuns: State[Stats, Unit] = for {
    _ <- _maxRuns %= { _ - 1 }
    _ <- _okRuns %= { _ + 1 }
  } yield ()

  val _increaseFailedRuns: State[Stats, Unit] = for {
    _ <- _maxRuns %= { _ - 1 }
    _ <- _failedRuns %= { _ + 1 }
  } yield ()

  implicit val statsShow: Show[Stats] = Show.shows { stats =>
    f"Remaining runs: ${BLACK}${stats.maxRuns}${RESET} " +
      f"failedRuns: ${RED}${stats.failedRuns}${RESET} " +
      f"okRuns: ${GREEN}${stats.okRuns}${RESET}"
  }
}
