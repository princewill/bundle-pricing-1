lazy val `bundle-pricing` = (project in file(".")).
  settings(
    name := "bundle-pricing",
    version := "1.0",
    scalaVersion := "2.11.5",
    libraryDependencies ++= Seq(
      "org.joda" % "joda-money" % "0.10.0",
      "org.specs2" %% "specs2-core" % "3.6" % "test"
    )
  )
