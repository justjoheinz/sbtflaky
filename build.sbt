import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

name := "sbtflaky"

version := "0.0.1"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
	"org.scalaz" %% "scalaz-core" % "7.1.0",
	"org.scalaz" %% "scalaz-effect" % "7.1.0"
	)
	
packageArchetype.java_application
