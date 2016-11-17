name := "secret-nick-2016"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  ws // Play web services
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
