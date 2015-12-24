import akka.actor._
import Server._
//import client._
import scala.io.Source
import com.typesafe.config.ConfigFactory
//import client.ConnectToServer
//import client.Client
//import server.Server

object BackendServer extends App{
      System.setProperty("java.net.preferIPv4Stack", "true")
      val system = ActorSystem("FBServer")
      val server = system.actorOf(Props(new FBServer), "ServerSystem")
      println("Backend Server Connected")
}