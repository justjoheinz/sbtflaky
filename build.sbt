import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.packager.Keys._


packageArchetype.java_application

name := "sbtflaky"

version := "0.0.1"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
	"org.scalaz" %% "scalaz-core" % "7.1.0",
	"org.scalaz" %% "scalaz-effect" % "7.1.0"
	)


