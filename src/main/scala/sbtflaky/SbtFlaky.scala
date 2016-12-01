package sbtflaky

import sbtflaky.ColorHelper._
import sbtflaky.ExitCode._
import sbtflaky.Stats._

import scala.Console._
import scala.sys.process._
import scalaz._
import Scalaz._


object SbtFlaky extends App with SbtTestRunner with CommandLineParser {

  for {
    config <- parser.parse(args, CommandLineConfig())
  } yield {
    config |> (startTestRuns _ andThen (printResult _).tupled)
  }

}


