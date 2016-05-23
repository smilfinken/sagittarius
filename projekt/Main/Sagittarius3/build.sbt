name := """Sagittarius3"""

version := "1.0-SNAPSHOT"
scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

val buildSettings = Defaults.coreDefaultSettings ++ Seq(
  javaOptions += "-Xmx512M -Xlint:unchecked"
)

libraryDependencies ++= Seq(
  cache,
  javaJpa,
  javaWs,
  evolutions,
  "org.hibernate" % "hibernate-entitymanager" % "5.1.0.Final",
  "it.innove" % "play2-pdf" % "1.5.0"
)

fork in run := true
