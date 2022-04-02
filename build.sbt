name := "mpp"

version := "1.0"

lazy val `mpp` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(jdbc, ehcache, ws, specs2 % Test, guice)
libraryDependencies += "com.zaxxer" % "HikariCP" % "4.0.3"
libraryDependencies += "org.springframework.security" % "spring-security-crypto" % "5.6.1"
libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.10"
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.28"
