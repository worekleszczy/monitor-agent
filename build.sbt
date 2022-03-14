import Dependencies._
import sbt.Package.{MainClass, ManifestAttributes}

ThisBuild / scalaVersion := "2.13.7"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "monitor-agent",
    libraryDependencies ++= Seq(scalaTest % Test, slf4j % "provided"),
    Compile / mainClass := Some("io.scalac.mesmer.utils.AgentMonitor"),
    Compile / packageBin / packageOptions := {
      (Compile / packageBin / packageOptions).value.map {
        case MainClass(mainClassName) =>
          ManifestAttributes(List("Premain-Class" -> mainClassName): _*)
        case other => other
      }
    },
    assembly / assemblyJarName := "monitor-akka-agent.jar",
    assembly / assemblyOption ~= {
      _.withIncludeScala(false)
    },
    assemblyMergeStrategySettings
  )

lazy val assemblyMergeStrategySettings = assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "services", _@_*) => MergeStrategy.concat
  case PathList("META-INF", _@_*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("jackson-annotations-2.10.3.jar", _@_*) => MergeStrategy.last
  case PathList("jackson-core-2.10.3.jar", _@_*) => MergeStrategy.last
  case PathList("jackson-databind-2.10.3.jar", _@_*) => MergeStrategy.last
  case PathList("jackson-dataformat-cbor-2.10.3.jar", _@_*) =>
    MergeStrategy.last
  case PathList("jackson-datatype-jdk8-2.10.3.jar", _@_*) => MergeStrategy.last
  case PathList("jackson-datatype-jsr310-2.10.3.jar", _@_*) =>
    MergeStrategy.last
  case PathList("jackson-module-parameter-names-2.10.3.jar", _@_*) =>
    MergeStrategy.last
  case PathList("jackson-module-paranamer-2.10.3.jar", _@_*) =>
    MergeStrategy.last
  case _ => MergeStrategy.first
}

