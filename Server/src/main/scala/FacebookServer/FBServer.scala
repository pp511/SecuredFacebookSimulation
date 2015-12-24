package Server

import _root_.Server.createPost
import akka.actor._
import akka.actor.{ActorSystem, Props}
import akka.actor.Actor
import collection.mutable.ListBuffer
import akka.routing.RoundRobinRouter
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
//import client._
//import common._
import java.io.FileWriter
import Server._
import akka.actor.{ActorSystem, Props}
import akka.actor.Actor
import akka.routing.RoundRobinRouter
import com.typesafe.config.ConfigFactory
import org.json4s.ShortTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import spray.json.DefaultJsonProtocol
import akka.actor.ActorSystem
import scala.collection.mutable
import scala.collection.mutable.SynchronizedBuffer
import scala.collection.mutable.SynchronizedMap
import play.api.libs.json.Json
import scala.util.parsing.json._
import java.security._
import java.security.spec.X509EncodedKeySpec
import javax.crypto._
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import javax.xml.bind.DatatypeConverter
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import javax.crypto._
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.X509EncodedKeySpec
import javax.crypto.spec.IvParameterSpec
import akka.actor._
import collection.mutable.ListBuffer
import akka.routing.RoundRobinRouter
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import java.io.FileWriter
import org.json4s.ShortTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import spray.json.DefaultJsonProtocol
import MyJsonProtocol._
import spray.json._
import Server._
import scala.collection.mutable._
import play.api.libs.json.Json
import java.security._
import java.security.spec.X509EncodedKeySpec
import javax.crypto._
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import javax.xml.bind.DatatypeConverter
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import javax.crypto._
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.X509EncodedKeySpec
import javax.crypto.spec.IvParameterSpec
import java.security.SecureRandom

//import common.Messages
case class TBD()

/* Functions of Server Class*/

case class createUser(createuserparam : String)

case class addFriend(id: Int, idf: Int)
case class searchUser(nameoffriend : String, lnameoffriend : String)
case class deleteFriend(id: Int, idf: Int)  // Not Implemented
//case class createPost(id: Int, post: String, postid :Int)
case class createPost(createpostparam : String)
case class viewPost(id: Int, idf : Int)
case class deletePost(id: Int, postId:Int) // Not Implemented
case class viewfriendList(id: Int, idf : Int)
case class viewPage(id: Int, idf : Int)
case class viewProfile(id: Int, idf : Int)


//
case class addProfilePic(id: Int ,profpic:String)
case class viewProfilePic(id: Int, idf : Int)
case class createAlbum(id: Int , albumid : Int)
//case class addPicstoAlbum(id: Int , albumid : Int ,pic: String, picid : Int)
case class addPicstoAlbum(albumpicparam : String)

case class viewAlbumPics(id: Int ,idf : Int, albumid : Int)

//
case class getPublicKey(id : Int, idf : Int)
case class setEncryptedKey(id : Int,idf : Int,encryptedkey : String)
case class getEncryptedKey(userid : Int,personid : Int)
case class updateHidekey(id : Int,idf : Int, postid : Int)
case class userAuthentication(id : Int,password : String)

  object ServerDetails {
    val serverPublicKey: String = "facebookServer"
    val serverPrivateKey: String = "serverfacebook"
    val serverId: String = "400"
  }

  class FBServer extends Actor {
    private implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[String])))
    import context.dispatcher
    System.setProperty("java.net.preferIPv4Stack", "true")
    val noOfWorkers = ((Runtime.getRuntime().availableProcessors()) * 1.5).toInt
    val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinRouter(noOfWorkers)), name = "workerRouter")
    var listOfusers = new ListBuffer[ActorRef]
    def receive = {
      case TBD() => {
      }
    }
  }
  class Worker extends Actor {

     def getprivkeyfromstring(key:String):PrivateKey ={
      val privateBytes = Base64.decodeBase64(key);
      val keySpec = new X509EncodedKeySpec(privateBytes);
      val keyFactory = KeyFactory.getInstance("RSA");
      keyFactory.generatePrivate(keySpec);
    }

    def getpubkeyfromstring(key:String):PublicKey ={
      val publicBytes = Base64.decodeBase64(key);
     // println(" getpubkeyfromstring text"+key)
      val keySpec = new X509EncodedKeySpec(publicBytes);
      val keyFactory = KeyFactory.getInstance("RSA");
      keyFactory.generatePublic(keySpec);
    }
    def encryptRSA(text:String,privatekey: String):String = {
      val key = getprivkeyfromstring(privatekey)
      var cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, key);
      var cipherText = cipher.doFinal(text.getBytes());
      var temp = Base64.encodeBase64String(cipherText);
      temp
    }

    private implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[String])))
    def receive = {

      case createUser(createuserparam : String) => {
/*
      println("Create User Receied")
      */
      val createUserrequest = Json.parse(createuserparam)
      val currentuser = new User(createUserrequest.\("userid").as[Int],
        createUserrequest.\("loginid").as[String],
        createUserrequest.\("password").as[String],
        createUserrequest.\("firstname").as[String],
        createUserrequest.\("lastname").as[String],
        createUserrequest.\("gender").as[String],
        createUserrequest.\("country").as[String],
        createUserrequest.\("city").as[String],
        createUserrequest.\("profession").as[String],
        createUserrequest.\("interestedin").as[String],
        createUserrequest.\("publickey").as[String] )

   // println(createUserrequest.\("userid").as[Int]+ " Created")
    Global.useridlist += createUserrequest.\("userid").as[Int]
    Global.usermap.put(createUserrequest.\("userid").as[Int], currentuser)
    sender ! "Success :User Account Created"
  }
    //*******************************************************************************************************************
      case searchUser(nameoffriend : String, lnameoffriend : String) => {
      //   println("findFriend request received")
      var nameList = new ListBuffer[Int]()
      for((key,value) <- Global.usermap)
      if(value.firstname.equals(nameoffriend)&& (value.lastname.equals(lnameoffriend))){
          //   println("Friend Found")
          nameList += value.userid
        }
        sender ! writePretty(nameList)
      }
    //*******************************************************************************************************************
    case addFriend(id: Int, idf: Int) => {
    //  println("Addfriend request received")

    if(Global.useridlist.exists(_ == idf)) {
      var currentuser = Global.usermap(id)
      var frienduser = Global.usermap(idf)

      var friend = new Friend(frienduser.firstname, frienduser.lastname)
      var current = new Friend(currentuser.firstname, currentuser.lastname)

      currentuser.friendlist += friend
      frienduser.friendlist += current

      currentuser.friendidlist += idf
      frienduser.friendidlist += id

      sender ! "Success :Friend Added"
    }
    else{
      sender ! "Error :User Does Not Exist"
    }
  }
    //******************************************************************************************************************
    case deleteFriend(id: Int, idf: Int) => { // To DO
  //    println("-----Exit")

  sender ! "Success :Friend Deleted"

}
    //******************************************************************************************************************
    case createPost(createpostparam : String) => {

    //  println("createPost request received")
    val createPostrequest = Json.parse(createpostparam)
    var id : Int = createPostrequest.\("userid").as[Int]
    val currentpost = new PostClass(createPostrequest.\("post").as[String],
      createPostrequest.\("postid").as[Int])

    currentpost.m_hiddenlist+=createPostrequest.\("hiddenlist").as[Int]
    var currentuser =  Global.usermap(id)

      var encryptedkeylist = createPostrequest.\("encryptedkeys").as[ListBuffer[String]]
      var iter = encryptedkeylist.iterator
      while (iter.hasNext) {
        var message = iter.next()
        val splitString: Array[java.lang.String] = message.split("\\,")
        //println(splitString(0)+" , "+splitString(1))
        var idf = splitString(0).toInt
        var key = splitString(1)
       // currentuser.postmap(createPostrequest.\("postid").as[Int]).encryptedkeymap.put(idf,key)
        currentpost.encryptedkeymap.put(idf,key)
      }
      //currentuser.lastpostid +=1;!
        currentuser.postmap.put(createPostrequest.\("postid").as[Int],currentpost)
      sender ! "Success :Post Created"
    }
    //*******************************************************************************************************************
    case viewPost(id: Int, idf : Int) => {
    //  println("viewPost request received")
   if(Global.useridlist.exists(_ == idf)) {
    if (isMutualFriend(id, idf)) {
      var postresponse = new mutable.ArrayBuffer[PostResponse]() with SynchronizedBuffer[PostResponse]

      var otheruser = Global.usermap(idf)
      for((key,value) <- otheruser.postmap){
           // val post = value
           if(!value.m_hiddenlist.exists(_ == id)){
                    if(otheruser.postmap.keySet.exists(_ == key)){
                      for((key2,value2) <- otheruser.postmap(key).encryptedkeymap) {
                        if(key2 == id){
                          postresponse += PostResponse(value.m_postid, value.m_post,value2 )
                        }
                      }
                    }
           }
         }
    //  println("viewPost "+postresponse)
         sender ! writePretty(postresponse)
       }
       else {
        sender ! writePretty("Error :You Are Not Allowed to Access Posts From This profile")
      }
      } else{
        sender ! writePretty("Error :User Does Not Exist")
      }
    }
    //********************************************************************************************************************
    case deletePost(id: Int, postId:Int) => { // To DO
      var currentuser =  Global.usermap(id)
      sender ! "Success :Post Deleted"
    }
    //*******************************************************************************************************************
    case viewfriendList(id: Int, idf : Int ) => {
  //    println("viewfriendList request received")
  if(Global.useridlist.exists(_ == idf)) {
    /*Ignoring User ID. To be used later in secured implemetation*/
    sender ! writePretty(Global.usermap(idf).friendlist)
  }
  else{
    sender ! "Error :User Does Not Exist"
  }

}
    //*******************************************************************************************************************
    case viewPage(id: Int, idf : Int) => {
  //    println("viewPage request received")
  if(Global.useridlist.exists(_ == idf)) {
    var otheruser = Global.usermap(idf)
     //   println("viewPage requested")
     if (isMutualFriend(id, idf)) {
      var postresponse =
      sender ! writePretty(PersonPage(
        otheruser.loginid,
        otheruser.firstname,
        otheruser.lastname,
        otheruser.gender,
        otheruser.country,
        otheruser.city,
        otheruser.profession,
        otheruser.interestedin,
        otheruser.friendlist,
        otheruser.albumidlist,
        otheruser.profilepic
        ))
      //    println("Provide")
    }
    else {
          // Limited access to profile since user is not a friend
          sender ! writePretty(LimitedProfile(
            otheruser.firstname,
            otheruser.lastname,
            otheruser.gender,
            otheruser.country,
            otheruser.city,
            otheruser.profession
            ))

        }
      }
      else{
        sender ! "Error :User Does Not Exist"
      }

    }
    //*******************************************************************************************************************
    case viewProfile(id: Int, idf : Int) => {
   //   println("viewProfile request received")
   if(Global.useridlist.exists(_ == idf)) {
    var otheruser = Global.usermap(idf)
    sender ! writePretty(LimitedProfile(
      otheruser.firstname,
      otheruser.lastname,
      otheruser.gender,
      otheruser.country,
      otheruser.city,
      otheruser.profession
      ))
  }
  else{
    sender ! "Error :User Does Not Exist"
  }

}
    //*******************************************************************************************************************
    case addProfilePic(id: Int , profpic:String) => {
  //    println("addProfilePic request received")
  Global.usermap(id).profilepic = profpic
  sender ! "Error :Profile Pic Added"
}
    //*******************************************************************************************************************

    case viewProfilePic(id: Int, idf : Int) => {
      if(Global.useridlist.exists(_ == idf)) {
        var otheruser =  Global.usermap(idf)
        sender ! writePretty(otheruser.profilepic)
      }
      else{
        sender ! "Error :User Does Not Exist"
      }
    }
    //*******************************************************************************************************************
    case createAlbum(id: Int , albumid : Int) => {
  //    println("createAlbum request received")
  var currentuser =  Global.usermap(id)
  if(currentuser.albumidlist.exists(_ == albumid)) {
    sender ! "Error :Album  Already Exists"
  }
  else {
    var newalbum = new Album(albumid)
    currentuser.albummap.put(albumid,newalbum)
    currentuser.albumidlist+=albumid
    sender ! "Success :Album  Created"
  }
}
    //*******************************************************************************************************************
      case addPicstoAlbum(albumpicparam : String) => {
//    case addPicstoAlbum(id: Int , albumid : Int ,pic:String, picid : Int) => {
    // println("addPicstoAlbum request received")

        val albumPicsparam = Json.parse(albumpicparam)

        var id = albumPicsparam.\("userid").as[Int]
        var albumid = albumPicsparam.\("albumid").as[Int]
        var pic =  albumPicsparam.\("pic").as[String]
        var picid =  albumPicsparam.\("picid").as[Int]
       //println("addPicstoAlbum pic"+ albumPicsparam.\("pic").as[String])
        var currentuser =  Global.usermap(id)
        if(currentuser.albumidlist.exists(_ == albumid)){      
        var photo = new Photo(pic)
        photo.m_hiddenlist+=albumPicsparam.\("hiddenlist").as[Int]
        currentuser.albummap(albumid).picmap.put(picid,photo)
        sender ! "Success :Pic Added in Album"
      }
      else{
        sender ! "Error :Album Does Not Exist"
      }

}
    //*******************************************************************************************************************
    case viewAlbumPics(id: Int , idf: Int, albumid : Int) => {
      if(Global.useridlist.exists(_ == idf)) {
        //   println("viewAlbumPics request received")

        if(isMutualFriend(id,idf)) {
          var otheruser = Global.usermap(idf)
          if (otheruser.albumidlist.exists(_ == albumid)) {
            var albumviewresp = new mutable.ArrayBuffer[PhotoResponse]() with SynchronizedBuffer[PhotoResponse]

            for ((key, value) <- otheruser.albummap(albumid).picmap) {
              // val post = value
              if (!value.m_hiddenlist.exists(_ == id))
                albumviewresp += PhotoResponse(key, value.m_photo)
            }
            sender ! writePretty(albumviewresp)
          }
          else {
            sender ! writePretty("Error :Album Does Not Exist")
          }
        }
          else{
            //    println("Album Does Not Exist")
            sender ! writePretty("Error : Unauthorised Acess")
          }

        }
        else{
          sender ! writePretty("Error :User Does Not Exist")
        }
      }
    //*******************************************************************************************************************
    case getPublicKey(id : Int, idf : Int) => {
      if(Global.useridlist.exists(_ == idf)) {
        if (isMutualFriend(id, idf)) {
          //println("getPublicKey",Global.usermap(idf).publickey)
          sender ! (Global.usermap(idf).publickey)
        }
        else {
          sender ! writePretty("Error :You Are Not Allowed to Access Public Key of this user")
        }
        } else{
          sender ! writePretty("Error User Does Not Exist")
        }

      }
    //*******************************************************************************************************************
    case setEncryptedKey(id : Int,idf : Int,encryptedkey : String) => {
      if(Global.useridlist.exists(_ == idf)) {
        if (isMutualFriend(id, idf)) {
          var currentuser =  Global.usermap(id)
          //println("setEncryptedKey" +encryptedkey)
          currentuser.encryptedkeymap.put(idf, encryptedkey)
          sender ! "Success :Encrypted key set"
        }
        else {
          sender ! "Error :The person is not in your friend list. Do you really wanna save his encrypted key!!!?"
        }
        } else{
          sender ! "Error :User Does Not Exist"
        }
      }
    //*******************************************************************************************************************
    case getEncryptedKey(id : Int,idf :Int) => {
      if(Global.useridlist.exists(_ == idf)) {
        if (isMutualFriend(id, idf)) {
          var otheruser =  Global.usermap(idf)
          if(otheruser.encryptedkeymap.keySet.exists(_ == id))    {
            sender ! (otheruser.encryptedkeymap(id))
          }
          else{
            sender ! writePretty("Error :You don't have key of this person")
          }
        }
        else {
          sender ! writePretty("Error :The person is not in your friend list. Are you out of your mind?")
        }
        } else{
          sender ! writePretty("Error :User Does Not Exist")
        }

      }
    //*******************************************************************************************************************
    case updateHidekey(id : Int,idf : Int, postid : Int) => {
      if(Global.useridlist.exists(_ == idf)) {
        if (isMutualFriend(id, idf)) {
          var currentuser =  Global.usermap(id)
          currentuser.postmap(postid).m_hiddenlist+=idf
          sender ! "Success :Updated Hide key"
        }
        else {
          sender ! "Error :The person is not in your friend list. There is not point in hiding the key. He can't view your posts"
        }
        } else{
          sender ! "Error :User Does Not Exist"
        }
      }
    //*******************************************************************************************************************
    case userAuthentication(userId: Int , cert: String) => {
      var userObject: User = null
      if(Global.useridlist.exists(_ == userId)){//(Global.usermap.keySet.exists(_ == userId)){

         if(cert.toLowerCase.contains("hello".toLowerCase()))
         {
          val secrand = new SecureRandom()
         // println(" userAuthentication "+secrand.toString)
          Global.usermap(userId).securerandomnumber = secrand.toString
          sender ! writePretty(secrand.toString )
         }
         else
         {
              userObject = Global.usermap(userId)

              var userpubKey = Global.usermap(userId).publickey
             userpubKey = userpubKey.replaceAll(" ","+")
        //    println("userAuthentication userpubKey"+userpubKey)
            val userpbKey = getpubkeyfromstring(userpubKey)

            var todecrypt = cert
            todecrypt = todecrypt.replaceAll(" ","+")
            if(todecrypt.endsWith("+"))      {
             todecrypt = todecrypt.substring(0,todecrypt.length() - 1)+"=";
           }

        //   println(" userAuthentication todecrypt " +todecrypt)
           var text = Base64.decodeBase64(todecrypt)
           var cipher = Cipher.getInstance("RSA")
           cipher.init(Cipher.DECRYPT_MODE, userpbKey)
           text = cipher.doFinal(text)
           var retvPwd = new String(text)
           // println("userAuthentication  Dectext "+retvPwd)
           retvPwd = retvPwd.replace(Global.usermap(userId).securerandomnumber,"")
           retvPwd = retvPwd.replace("\"","")
            // println("userAuthentication  Secrandom "+secrand.toString)
           //  println("userAuthentication  Dectext after "+retvPwd)
            var currpasswd = userObject.passwd.replaceAll(" ","+")
          //  println("userAuthentication  currpasswd  "+currpasswd)
           if(retvPwd == currpasswd){
            //val encryptedText = encryptRSA(ServerDetails.serverId, ServerDetails.serverPrivateKey)
             println("User Authentication Successfull")
              sender ! writePretty("Success")//loginResult("Success", new String(encryptedText)))
          }
        //}
          else{

             sender ! writePretty("Failure")//(loginResult("Failure", "Authentication Failed"))
             }
      }
    }
      else{
       // println(" userAuthentication Return path 1")
        sender ! writePretty("Failure")//(loginResult("Failure", "Authentication Failed"))
      }

    }
    //*******************************************************************************************************************
  }
  def isMutualFriend(id: Int, idf : Int): Boolean ={
    if(id == idf)
    return true
    var currentuser =  Global.usermap(id)
    var seconduser =  Global.usermap(id)
    return currentuser.friendidlist.exists(_ == idf)
  }

}