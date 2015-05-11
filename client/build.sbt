import android.Keys._

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

android.Plugin.androidBuild

name := "client"

scalaVersion := "2.11.6"

proguardCache in Android ++= Seq(
  ProguardCache("org.scaloid") % "org.scaloid"
)

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize", "-keepattributes Signature", "-printseeds target/seeds.txt", "-printusage target/usage.txt"
  , "-dontwarn scala.collection.**" // required from Scala 2.11.4
  , "-dontwarn org.scaloid.**" // this can be omitted if current Android Build target is android-16
  , "-dontwarn **" // manually added
)

libraryDependencies ++= Seq(
  "org.scaloid" %% "scaloid" % "4.0-RC1" withSources() withJavadoc()
  , "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
  , "io.argonaut" %% "argonaut" % "6.0.4"
  , "org.scalatest" % "scalatest_2.11" % "2.2.4" % Test
  , "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % Test
)



scalaSource in Test := baseDirectory.value / "test" / "scala"

scalacOptions in Compile += "-feature"

run <<= run in Android

install <<= install in Android

retrolambdaEnable in Android := false