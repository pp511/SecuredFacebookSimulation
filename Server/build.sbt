name := "Server"
version       := "0.1"
scalaVersion  := "2.11.2"
scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"	  
libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV withSources() withJavadoc(),
    "io.spray"            %%  "spray-routing" % sprayV withSources() withJavadoc(),
    "io.spray"            %%  "spray-json"    % "1.3.1",
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %% "akka-remote"    % "2.3.7",
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "org.scalaz"          %%  "scalaz-core"   % "7.1.0",
    "org.json4s" %% "json4s-native" % "3.2.10",
    "com.typesafe.play"   %%  "play-json"     % "2.3.0"
  )
}
libraryDependencies += "commons-lang" % "commons-lang" % "2.6" 
libraryDependencies += "commons-codec" % "commons-codec" % "1.9"




