//package org.smartjava;
package Server
import _root_.Server._
import _root_.Server.addFriend
import _root_.Server.addFriend
import _root_.Server.createPost
import _root_.Server.searchUser
import _root_.Server.viewAlbumPics
import _root_.Server.viewPost
import _root_.Server.viewProfile
import _root_.Server.viewfriendList
import _root_.Server.viewPage
import akka.actor.{ActorSystem, Props}
import akka.actor.Actor
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import spray.http._
import spray.routing._
import spray.httpx.SprayJsonSupport._
import scala.concurrent.duration._
import spray.json.DefaultJsonProtocol
import MediaTypes._
import Server._
import akka.actor.ActorSelection
import akka.pattern.AskTimeoutException
import concurrent._
import concurrent.duration._
import java.util.concurrent.Executors
import java.net.URLEncoder
import java.net.URLDecoder
import java.util.concurrent.TimeUnit

object Stats {
  var starttime : Long = 0
  var Requests=0
  var Max_Average : Long=0
  val Max_Average_Possible=7000 // Exceeding it means time time elapsed time somehow gave incorret value. This is just an estimation
}

object WebServerBoot extends App {
  // create our actor system with the name smartjava
  implicit val system = ActorSystem("Server")
  val service = system.actorOf(Props(new WebServer(system)), "Web-Server")
  // IO requires an implicit ActorSystem, and ? requires an implicit timeout
  // Bind HTTP to the specified service.
  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
//  val server = system.actorOf(Props(new FBServer),"FBBackendServer") // Going to use Server as a different entity.
}

// simple actor that handles the routes.
class WebServer(system: ActorSystem) extends Actor with HttpService {

  import system.dispatcher

//  implicit val pp = context.system
 // import pp.dispatcher
  context.system.scheduler.schedule(Duration.create(120000, TimeUnit.MILLISECONDS),
    Duration.create(10000, TimeUnit.MILLISECONDS))(printStatus)

  Stats.starttime = System.currentTimeMillis()
  context.system.scheduler.schedule(Duration.create(10000, TimeUnit.MILLISECONDS),
    Duration.create(10000, TimeUnit.MILLISECONDS))(printStatus)

  def actorRefFactory = context

  implicit val timeout = Timeout(2.seconds)

  val connectTo = "akka.tcp://FBServer@127.0.0.1:8081/user/ServerSystem/workerRouter"
  val server = system.actorSelection(connectTo)

  def receive = runRoute(createuser ~ addfriend ~ searchuser ~ deletefriend ~ createpost ~ viewpost ~ deletepost ~ viewfriendlist ~ viewpage ~ viewprofile ~ addprofilepic ~ viewprofilepic ~ createalbum ~ addpicstoalbum ~ viewalbumpics ~ getpublickey ~ setencryptedkey ~ getencryptedkey ~ updatehidekey ~ userauthentication)

  val createuser = {
    path("createuser") {
      post {
       // println(Stats.Requests)
        parameter("createuserparam") { createuserparam =>
          complete {
            Stats.Requests+=1
            (server ? createUser(URLDecoder.decode(createuserparam, "UTF-8"))).recover {
              // (server ? createUser(createuserparam)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")
          }
        }
      }
    }
  }
  //*******************************************************************************************************************
  val addfriend = {
    path("addfriend") {
      post {
        parameter("userid", "friendid") { (userid, friendid) =>
          complete {
            Stats.Requests+=1

            (server ? addFriend(userid.toInt, friendid.toInt)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*******************************************************************************************************************
  val searchuser = {
    path("searchuser") {
      get {
        parameter("firstname", "lastname") { (firstname, lastname) =>
          complete {
            Stats.Requests+=1

            (server ? searchUser(firstname, lastname)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*******************************************************************************************************************
  val deletefriend = {
    // To DO
    path("deletefriend") {
      post {
        parameter("userid", "friendid") { (userid, friendid) =>
          complete {
            Stats.Requests+=1

            (server ? deleteFriend(userid.toInt, friendid.toInt)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*******************************************************************************************************************
  val createpost = {
    path("createpost") {
      post {
        parameter("createpostparam") { createpostparam =>
          complete {
            Stats.Requests+=1

            (server ? createPost(URLDecoder.decode(createpostparam, "UTF-8"))).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*******************************************************************************************************************
  val viewpost = {
    path("viewpost") {
      get {
        parameter("userid", "personid") { (userid, personid) =>
          complete {
            Stats.Requests+=1

            (server ? viewPost(userid.toInt, personid.toInt)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*******************************************************************************************************************
  val deletepost = {
    // To DO
    path("deletepost") {
      post {
        parameter("userid", "postid") { (userid, postid) =>
          complete("delete-post")
        }
      }
    }
  }
  //*******************************************************************************************************************
  val viewfriendlist = {
    path("viewfriendlist") {
      get {
        parameter("userid", "personid") { (userid, personid) =>
          complete {
            Stats.Requests+=1

            (server ? viewfriendList(userid.toInt, personid.toInt)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*********************************************************i**********************************************************
  val viewpage = {
    path("viewpage") {
      get {
        parameter("userid", "personid") { (userid, personid) =>
          complete {
            Stats.Requests+=1

            (server ? viewPage(userid.toInt, personid.toInt)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*********************************************************i**********************************************************
  val viewprofile = {
    path("viewprofile") {
      get {
        parameter("userid", "personid") { (userid, personid) =>
          complete {
            Stats.Requests+=1

            (server ? viewProfile(userid.toInt, personid.toInt)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*********************************************************i**********************************************************
  val addprofilepic = {
    path("addprofilepic") {
      post {
        parameter("userid", "pic") { (userid, pic) =>
          complete {
            Stats.Requests+=1

            //  (server ? addProfilePic(userid.toInt,pic.getBytes("UTF-8"))).recover {
            (server ? addProfilePic(userid.toInt, pic)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*********************************************************i**********************************************************
  val viewprofilepic = {
    path("viewprofilepic") {
      get {
        parameter("userid", "personid") { (userid, personid) =>
          complete {
            Stats.Requests+=1

            (server ? viewProfilePic(userid.toInt, personid.toInt)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*********************************************************i**********************************************************
  val createalbum = {
    path("createalbum") {
      post {
        parameter("userid", "albumid") { (userid, albumid) =>
          complete {
            Stats.Requests+=1
            (server ? createAlbum(userid.toInt, albumid.toInt)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*********************************************************i**********************************************************
  val addpicstoalbum = {
    path("addpicstoalbum") {
      post {
        parameter("albumpicparam") {albumpicparam =>
          complete {
            Stats.Requests+=1
            (server ? addPicstoAlbum(URLDecoder.decode(albumpicparam, "UTF-8"))).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*********************************************************i**********************************************************
  val viewalbumpics = {
    path("viewalbumpics") {
      get {
        parameter("userid", "personid", "albumid") { (userid, personid, albumid) =>
          complete {
            Stats.Requests+=1
            (server ? viewAlbumPics(userid.toInt, personid.toInt, albumid.toInt)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")
          }
        }
      }
    }
  }
  //*******************************************************************************************************************
  val getpublickey = {
    path("getpublickey") {
      get {
        parameter("userid", "personid") { (userid, personid) =>
          complete {
            Stats.Requests+=1
            (server ? getPublicKey(userid.toInt, personid.toInt)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }

  //*******************************************************************************************************************
  val setencryptedkey = {
    path("setencryptedkey") {
      post {
        parameter("userid", "personid", "encryptedkey") { (userid, personid, encryptedkey) =>
          complete {
            Stats.Requests+=1
            (server ? setEncryptedKey(userid.toInt, personid.toInt, encryptedkey)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }

  //*******************************************************************************************************************
  val getencryptedkey = {
    path("getencryptedkey") {
      get {
        parameter("userid", "personid") { (userid, personid) =>
          complete {
            Stats.Requests+=1
            (server ? getEncryptedKey(userid.toInt, personid.toInt)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*******************************************************************************************************************
  val updatehidekey = {
    path("updatehidekey") {
      post {
        parameter("userid", "personid", "postid") { (userid, personid, postid) =>
          complete {
            Stats.Requests+=1
            (server ? updateHidekey(userid.toInt, personid.toInt, postid.toInt)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*******************************************************************************************************************
  val userauthentication = {
    path("userauthentication") {
      post {
        parameter("userid", "cert") { (userid, cert) =>
          complete {
            Stats.Requests+=1
          //  println( Stats.Requests)
            (server ? userAuthentication(userid.toInt, cert)).recover {
              case ex: AskTimeoutException => {
                "Error: request failed (Reason Timeout Exception)"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }

  def printStatus() = {
     Stats.Requests+=1
    var timeElapsed = (System.currentTimeMillis() - Stats.starttime) / 1000
    var avg = Stats.Requests / (timeElapsed + 1)
    if (avg < Stats.Max_Average_Possible) {
      if ((avg > Stats.Max_Average)) {
        // && (avg < 0X1FFF)){
        Stats.Max_Average = avg
      }
      Stats.Requests = 0
      Stats.starttime = System.currentTimeMillis()
    }
    println(
      "******************************************************************************************************"+
        "\n Current Average(of Last 10 sec) = "+avg+
        "\n Max Average Requests PerSecond(Over period of 10 sec)= ***** "+Stats.Max_Average+" ********"+
        "\n *************************************************************************************************"
    )
  }


}



