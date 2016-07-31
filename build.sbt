name := "kafka-test"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "0.10.0.0",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.11-M4"
)

fork in run := true

cancelable in Global := true