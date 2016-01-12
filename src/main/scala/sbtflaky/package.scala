import scalaz._
import Scalaz._
import Free.Trampoline
package object sbtflaky {
  type Log = Vector[String]
  // Reader = Unit, Writer = Log, State = Stats, Result = A
  type SbtRun[A] = ReaderWriterState[String, Log, Stats, A]
}