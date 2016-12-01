package sbtflaky


object ColorHelper {
  val AnsiColors = scala.io.AnsiColor
  import AnsiColors._
  def colored(color: String, str: String)  = s"$color$str$RESET"
}
