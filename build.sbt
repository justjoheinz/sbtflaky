import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.packager.Keys._
import ReleaseTransformations._


packageArchetype.java_application

name := "sbtflaky"


scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
	"org.scalaz" %% "scalaz-core" % "7.2.0",
	"org.scalaz" %% "scalaz-effect" % "7.2.0",
	"com.github.scopt" %% "scopt" % "3.2.0",
	"org.specs2" %% "specs2-core" % "3.7" % "test"
	)

scalacOptions in Test ++= Seq("-Yrangepos")

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,              // : ReleaseStep
  inquireVersions,                        // : ReleaseStep
  runTest,                                // : ReleaseStep
  setReleaseVersion,                      // : ReleaseStep
  commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
  tagRelease,                             // : ReleaseStep
  //  publishArtifacts,                   // : ReleaseStep, checks whether `publishTo` is properly set up
  setNextVersion,                         // : ReleaseStep
  commitNextVersion,                      // : ReleaseStep
  pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
)


lazy val root = (project in file(".")).
	enablePlugins(BuildInfoPlugin).
	settings(
		buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
		buildInfoPackage := "sbtflaky"
	)

