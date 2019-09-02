name := "sbt_hello"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.17"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.9"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.25"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.12"
libraryDependencies += "ch.megard" %% "akka-http-cors" % "0.4.1"