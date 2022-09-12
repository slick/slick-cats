import _root_.io.github.nafg.mergify.dsl._
import _root_.io.github.nafg.mergify.Condition


name := "slick-cats-parent"

sourcesInBase := false
publish / skip := true

WriteMergify.mergifyScalaStewardConditions := {
  val jobsCondition =
    WriteMergify.workflowJobCheckNames(githubWorkflowGeneratedCI.value.filter(_.id == "build"))
      .map(Attr.CheckSuccess :== _)
      .reduce[Condition](_ && _)
  val authorsCondition =
    Seq("scala-steward", "renovate[bot]")
      .map(Attr.Author :== _)
      .reduce[Condition](_ || _)
  Seq(authorsCondition && jobsCondition)
}

inThisBuild(Seq(
  organization := "com.rms.miu",

  scalaVersion := "2.12.12",
  crossScalaVersions := Seq("2.12.12", "2.13.4"),

  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard"
  )
))

val catsVersion = "2.8.0"

lazy val slickcats =
  project.in(file("slick-cats"))
    .settings(
      name := "slick-cats",
      description := "Cats instances for Slick's DBIO",
      libraryDependencies ++= Seq(
        "com.typesafe.slick" %% "slick" % "3.4.0",
        "org.typelevel" %% "cats-core" % catsVersion,
        "org.typelevel" %% "cats-laws" % catsVersion % Test,
        "org.typelevel" %% "discipline-scalatest" % "2.2.0" % Test,
        "org.scalatest" %% "scalatest" % "3.2.13" % Test,
        "org.scalacheck" %% "scalacheck" % "1.16.0" % Test
      )
    )

lazy val docs =
  project.in(file("slick-cats-docs"))
    .dependsOn(slickcats)
    .enablePlugins(MdocPlugin)
    .settings(
      name := "slick-cats-docs",
      mdoc / scalacOptions --= Seq("-Ywarn-unused-import", "-Xlint"),
      publish / skip := true
    )

licenses += ("BSD New", url("https://opensource.org/licenses/BSD-3-Clause"))
homepage := Some(url("https://github.com/rmsone/slick-cats"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/rmsone/slick-cats"),
    "scm:git@github.com:rmsone/slick-cats.git"
  )
)
developers := List(
  Developer(id = "23will", name = "William Duncan", email = "", url("https://github.com/23will")),
  Developer(id = "tvaroh", name = "Alexander Semenov", email = "", url("https://github.com/tvaroh")),
  Developer(id = "frosforever", name = "Yosef Fertel", email = "", url("https://github.com/frosforever"))
)

publishMavenStyle := true
publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)
