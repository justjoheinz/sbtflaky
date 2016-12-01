import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.packager.Keys._
import ReleaseTransformations._


name := "sbtflaky"

scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
	"org.scalaz" %% "scalaz-core" % "7.2.7",
	"org.scalaz" %% "scalaz-effect" % "7.2.7",
	"com.github.scopt" %% "scopt" % "3.5.0",
	"org.specs2" %% "specs2-core" % "3.8.6" % "test"
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
	enablePlugins(BuildInfoPlugin, JavaAppPackaging).
	settings(
		buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
		buildInfoPackage := "sbtflaky"
	)

