package sbtflaky

import scala.io.AnsiColor._

object ColorHelper {
  def colored(color: String, str: String)  = s"$color$str$RESET"
}
