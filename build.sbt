name := "twitter-sentiment-analyzer"

version := "0.0.1"

scalaVersion := "2.13.6"

Compile / run / fork := true

libraryDependencies ++= Seq(
  "edu.stanford.nlp" % "stanford-corenlp" % "4.2.2",
  "edu.stanford.nlp" % "stanford-corenlp" % "4.2.2" classifier "models",
  "com.typesafe.akka" %% "akka-stream" % "2.6.15",
  "com.typesafe.akka" %% "akka-http" % "10.2.6",
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "3.0.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "ch.qos.logback" % "logback-classic" % "1.2.5",
  "io.circe" %% "circe-core" % "0.14.1",
  "io.circe" %% "circe-generic" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1"
  )
