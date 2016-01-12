package sbtflaky

import scalaz._

/**
 * Values for the command line option
 */
case class CommandLineConfig(nrRuns: Int = 10, cmd: List[String] = Nil) {
  def fullCommand : String = s"${cmd.mkString(" ")}"
}


object CommandLineConfig {
  // Show typeclass
  implicit def cmdLineConfigShows: Show[CommandLineConfig] = new Show[CommandLineConfig] {
    override def show(c: CommandLineConfig): Cord = s"(${c.nrRuns}, ${c.fullCommand}"
  }
}

object CommandLineParser {
  val parser = new scopt.OptionParser[CommandLineConfig]("sbtflaky") {
    head("sbtflaky", BuildInfo.version)
    opt[Int]("num") required() action { (cNrRuns, c) =>
      c.copy(nrRuns = cNrRuns)
    } validate { cNrRuns =>
      if (cNrRuns > 0) success else failure("numberRuns must be greater zero")
    } text ("execute the test a specified number of times")
    arg[String]("<sbt test command>") required() unbounded() action { (cCmd, c) =>
      c.copy(cmd = c.cmd :+ cCmd)
    } text ("the sbt command to execute")
    help("help") text ("prints this usage text")
    note( """|
            |Examples:
            |  sbtflaky -n 10 testOnly MySpec
            |  sbtflaky -n 3 testOnly *DBSpec
            |
            |The output of all failed tests will be piped to stdout.""".stripMargin)
  }

}
