package Client
import akka.actor.ActorSystem
import spray.http.BasicHttpCredentials
import spray.client.pipelining._
import akka.actor.Actor
import akka.actor.Cancellable
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
//import org.apache.commons.httpclient.util.URIUtil
import scala.util.Random
import spray.http.HttpRequest
import java.io.RandomAccessFile
import java.util.Date
import java.io.File
import akka.actor.Props
import spray.http.HttpResponse
import Client._

object ClientSimulator extends App {

  implicit val system = ActorSystem()

  import system.dispatcher

  val pipeline = sendReceive

  Global.totalUsersCount = 100
  println("Creating Userss")

  for (i <- 0 until Global.totalUsersCount) {
    val actor = system.actorOf(Props(new User(i,Global.totalUsersCount)),"" + i)
    Thread.sleep(100)
    if(i%100 == 0)
      println(i+" Users Created")

  }
  println(Global.totalUsersCount +" Users created. Now Simulating  User Behaviour")


  for (i <- 0 until Global.totalUsersCount) {
    var actor = system.actorSelection("/user/" + i)
     Thread.sleep(100)
    if(i%100 == 0)
      println(i+" Users Active")
    actor ! Authenticate
/*    Thread.sleep(100)
    actor ! UserStart*/
  }

/*  println("Closing the System")

  Thread.sleep(60000L)
  for (i <- 1 to Global.totalUsersCount) {  
    var actor = system.actorSelection("/user/" + i)
    Thread.sleep(100)
    println(i+" Users Deactivated")
    actor ! UserStop
  }*/
}
