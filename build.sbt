lazy val `bundle-pricing` = (project in file(".")).
  settings(
    name := "bundle-pricing",
    version := "1.0",
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      "org.joda" % "joda-money" % "0.10.0",
      "org.joda" % "joda-convert" % "1.7",
      "org.specs2" %% "specs2-core" % "3.6" % "test",
      "com.storm-enroute" %% "scalameter" % "0.6" % "test"
    ),
    resolvers ++= Seq(
      "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases",
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"),
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    logBuffered := false,
    parallelExecution in Test := false
  )
