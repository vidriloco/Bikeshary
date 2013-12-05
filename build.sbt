name := "Bikeshary"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm
)     

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.9.5" exclude("org.scala-stm", "scala-stm_2.10.0"),
  "com.cloudphysics" % "jerkson_2.10" % "0.6.3",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "com.vividsolutions" % "jts" % "1.11"
)

play.Project.playScalaSettings
