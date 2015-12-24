name := "Client"
version := "1.0"
scalaVersion := "2.11.2"
scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
resolvers += "spray repo" at "http://repo.spray.io"
val sprayVersion = "1.3.2"
libraryDependencies ++= Seq(
"com.typesafe.akka" %% "akka-actor" % "2.3.5",
"com.typesafe.akka" %% "akka-http-experimental" % "0.7",
"io.spray" %% "spray-routing" % sprayVersion,
"io.spray" %% "spray-client" % sprayVersion,
"com.typesafe.akka"   %% "akka-remote"    % "2.3.7",
"io.spray" %% "spray-testkit" % sprayVersion % "test",
"org.json4s" %% "json4s-native" % "3.2.10",
"io.spray" %%  "spray-json"    % "1.3.1",
"com.typesafe.play"   %%  "play-json"     % "2.3.0",
"io.spray"            %%  "spray-json"        % "1.2.6"
)
libraryDependencies += "commons-lang" % "commons-lang" % "2.6"
libraryDependencies += "commons-codec" % "commons-codec" % "1.9"

