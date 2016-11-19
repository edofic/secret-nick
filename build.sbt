name := "secret-nick-2016"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  ws, // Play web services
  evolutions,
  jdbc,
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc42"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
