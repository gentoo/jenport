scalaVersion := "2.11.6"
scalaHome := Some(file("/usr/share/scala-2.11"))

scalacOptions ++= Seq("-deprecation", "-feature")

val aetherGroupId = "org.eclipse.aether"
val aetherVersion = "[1.0.2.v20150114,)"
val mavenGroupId = "org.apache.maven"
val mavenVersion = "[3.3.3,)"
val wagonGroupId = "org.apache.maven.wagon"
val wagonVersion = "[2.9,)"

libraryDependencies ++= Seq(
  aetherGroupId % "aether-api" % aetherVersion,
  aetherGroupId % "aether-util" % aetherVersion,
  aetherGroupId % "aether-impl" % aetherVersion,
  aetherGroupId % "aether-connector-basic" % aetherVersion,
  aetherGroupId % "aether-transport-file" % aetherVersion,
  aetherGroupId % "aether-transport-http" % aetherVersion,
  aetherGroupId % "aether-transport-wagon" % aetherVersion,
  mavenGroupId % "maven-aether-provider" % mavenVersion,
  mavenGroupId % "maven-model" % mavenVersion,
  wagonGroupId % "wagon-ssh" % wagonVersion,
  "com.google.inject" % "guice" % "3+"
)

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)
