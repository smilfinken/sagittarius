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
  "it.innove" % "play2-pdf" % "1.5.0",
  "org.pac4j" % "play-pac4j" % "2.1.0",
  "org.pac4j" % "pac4j-oauth" % "1.8.8",
  "org.pac4j" % "pac4j-openid" % "1.8.8"
)

fork in run := false
routesGenerator := InjectedRoutesGenerator

herokuAppName in Compile := "secret-mesa-95653"
