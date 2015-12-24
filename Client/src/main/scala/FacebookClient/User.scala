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
import java.net.URLEncoder
import spray.json.DefaultJsonProtocol
import java.security.MessageDigest
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import MyJsonProtocol._
import spray.json._
import javax.crypto._
import java.security.PublicKey
import scala.util.Failure
import scala.util.Random
import scala.util.Success
import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Cancellable
import akka.actor.Props
import akka.actor.Terminated
import akka.actor.actorRef2Scala
import akka.event.LoggingReceive
import spray.client.pipelining._
import spray.http.ContentTypes
import spray.http.HttpEntity
import spray.http.HttpMethods.GET
import spray.http.HttpMethods.POST
import spray.http.HttpRequest
import spray.http.HttpResponse
import spray.http.Uri.apply
import spray.json.pimpAny
import spray.json.pimpString
import scala.concurrent.Await
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import akka.util.Timeout
import scala.concurrent.Future
import scala.collection.mutable
import scala.collection.mutable.SynchronizedBuffer
import scala.collection.mutable.{SynchronizedMap,ListBuffer}

case class UserStart()
case class UserStop()
case class notification(createdby:Int)
case class updateencryptedkey(userid:Int, friendid:Int)
case class Authenticate()
class User(userid: Int, maxUser: Int) extends Actor {
  implicit val system = context.system
  import system.dispatcher
  Global.currentUserCount+=1

  val pipeline = sendReceive

  var addfriend : Cancellable =null;
  var createpost : Cancellable = null;
  var viewfriendlist : Cancellable = null;
  var viewpost: Cancellable = null;
  var viewpage: Cancellable = null;
  var searchuser : Cancellable = null;
  var deletefriend : Cancellable = null;
  var viewprofile : Cancellable = null;
  //Album Related API
  var addprofilepic : Cancellable =null;
  var viewprofilepic : Cancellable =null;
  var createalbum : Cancellable =null;
  var addpicstoalbum : Cancellable =null;
  var viewalbumpics : Cancellable =null;
  var hideposts: Cancellable =null;
 
  var randhighselect = Random.nextInt(20)
  var randlowselect = Random.nextInt(10)
  var select = userid
  if(maxUser > 1000)
  select = Random.nextInt(1000)

  val Mypost = 1 + Random.nextInt(maxUser);
  val Myfriend = 5// + Random.nextInt(maxUser);
  val Mypageview = 1+ Random.nextInt(maxUser);
  val Myalbum = 1+ Random.nextInt(10) //Max 10 albums of each user

  var countpost  = 0
  var countfriend = 0
  var countpageview = 0
  var countalbum = 0
  var countpic = 0
  val usertype =  1 //userid % 6   // Simjulate user behaviour 0,1,2 Most active 3,4, moderately active and 5 rearely active
  
  var friendset = scala.collection.mutable.Set[Int]()
 // println(self.path)
  

/*
   pipeline(Post(Global.CreateUser + userid + "&loginid=" + Global.loginidlist(select) + "&password=" + Global.passlist(select) + "&firstname=" + Global.namelist(select) + "&lastname=" + Global.lnamelist(select) + "&gender=" + Global.genderlist(randlowselect) + "&country=" + Global.countrylist(randlowselect) + "&city=" + Global.citylist(randlowselect) + "&profession=" + Global.professionlist(randhighselect) + "&interestedin=" + Global.interestlist(randhighselect)))
   */
   val keyAES =  encryption.generateAESkey
   val symkey = keyAES.replaceAll("=","")
   val iv = generateIv("1");
   val keypair = encryption.generateSymKey
   val encrypasswd =  encryption.encryptWithAESKey(Global.passlist(select),symkey,iv)
 //  println("createuser",userid)
 //  println("symkey" +symkey)
   var requestParam =  new CreateUserParam(userid,
    Global.loginidlist(select),
    encrypasswd,
    Global.namelist(select),
    Global.lnamelist(select),
    Global.genderlist(randlowselect),
    Global.countrylist(randlowselect),
    Global.citylist(randlowselect),
    Global.professionlist(randhighselect),
    Global.interestlist(randhighselect),
    encryption.generatepublickeystring(keypair.getPublic)
    )
   Global.Pubkeymap.put(userid, keypair.getPublic)
   var encodedString = URLEncoder.encode(requestParam.toJson+"","UTF-8")
   val result = pipeline(Post(Global.CreateUser+encodedString))
 displayResult(result)
 def receive = {
  case Authenticate =>
   var toserver = "Hello"
   val result2 = pipeline(Post(Global.UserAuthentication+userid+"&cert="+toserver))
   var res1: String=""
    result2.foreach{
      response=>
        res1 =(s"${response.entity.asString}")
  //  println(" Authenticate "+res1+encrypasswd)
    var passauth = encryption.encryptRSA(res1+encrypasswd,keypair.getPrivate)
    if(passauth.endsWith("="))
    {
      passauth = passauth.substring(0,passauth.length() - 1)+"+";
    }
    // println("Authentication passauth"+passauth)
    Thread.sleep(100)
    val result1 = pipeline(Post(Global.UserAuthentication+userid+"&cert="+passauth))
    var res: String=""
    result1.foreach{
      response=>
        res =(s"${response.entity.asString}")
        //val splitString:Array[java.lang.String]= temp.split("\\,")
        //val auth = splitString(0)
        println("UserAuthentication "+res)
        if(res.toLowerCase().contains("Success".toLowerCase()) == true)
        self ! UserStart // start activities if authentication successfull
    }
  }


  case UserStart =>
  usertype match{
    case 0 | 1| 2=>
    addfriend = context.system.scheduler.schedule(Duration.create(10000, TimeUnit.MILLISECONDS),
      Duration.create(500, TimeUnit.MILLISECONDS))(addFriendfun)
  createalbum = context.system.scheduler.schedule(Duration.create(10000, TimeUnit.MILLISECONDS),
          Duration.create(1600, TimeUnit.MILLISECONDS))(createalbumFun)
/*   addpicstoalbum = context.system.scheduler.schedule(Duration.create(10000, TimeUnit.MILLISECONDS),
          Duration.create(1700, TimeUnit.MILLISECONDS))(addpicstoalbumFun)
   viewalbumpics = context.system.scheduler.schedule(Duration.create(10000, TimeUnit.MILLISECONDS),
          Duration.create(2200, TimeUnit.MILLISECONDS))(viewalbumpicsFun)*/
    viewpage = context.system.scheduler.schedule(Duration.create(300, TimeUnit.MILLISECONDS),
      Duration.create(1000, TimeUnit.MILLISECONDS))(viewpageFun)
createpost =context.system.scheduler.schedule(Duration.create(12000, TimeUnit.MILLISECONDS),
  Duration.create(1000, TimeUnit.MILLISECONDS))(createpostFun)
  viewpost = context.system.scheduler.schedule(Duration.create(15000, TimeUnit.MILLISECONDS),
    Duration.create(2000, TimeUnit.MILLISECONDS))(viewpostFun)
   viewfriendlist=context.system.scheduler.schedule(Duration.create(600, TimeUnit.MILLISECONDS),
          Duration.create(1800, TimeUnit.MILLISECONDS))(viewfriendlistFun)
    searchuser=context.system.scheduler.schedule(Duration.create(800, TimeUnit.MILLISECONDS),
          Duration.create(1200, TimeUnit.MILLISECONDS))(searchuserFun)
   deletefriend=context.system.scheduler.schedule(Duration.create(900, TimeUnit.MILLISECONDS),
          Duration.create(10000, TimeUnit.MILLISECONDS))(deletefriendFun)
   addprofilepic = context.system.scheduler.schedule(Duration.create(1000, TimeUnit.MILLISECONDS),
          Duration.create(3000, TimeUnit.MILLISECONDS))(addprofilepicFun)
   viewprofilepic = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(800, TimeUnit.MILLISECONDS))(viewprofilepicFun)
   viewprofile= context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(1400, TimeUnit.MILLISECONDS))(viewprofileFun)
     hideposts = context.system.scheduler.schedule(Duration.create(1500, TimeUnit.MILLISECONDS),
    Duration.create(5000, TimeUnit.MILLISECONDS))(hidepostFun)
  /*  case 3 | 4=>
     addfriend = context.system.scheduler.schedule(Duration.create(200, TimeUnit.MILLISECONDS),
          Duration.create(4000, TimeUnit.MILLISECONDS))(addFriendfun)
    viewpage = context.system.scheduler.schedule(Duration.create(300, TimeUnit.MILLISECONDS),
          Duration.create(2000, TimeUnit.MILLISECONDS))(viewpageFun)
    createpost =context.system.scheduler.schedule(Duration.create(400, TimeUnit.MILLISECONDS),
          Duration.create(3000, TimeUnit.MILLISECONDS))(createpostFun)
    viewpost = context.system.scheduler.schedule(Duration.create(500, TimeUnit.MILLISECONDS),
          Duration.create(1000, TimeUnit.MILLISECONDS))(viewpostFun)
   viewfriendlist=context.system.scheduler.schedule(Duration.create(600, TimeUnit.MILLISECONDS),
          Duration.create(3600, TimeUnit.MILLISECONDS))(viewfriendlistFun)
    searchuser=context.system.scheduler.schedule(Duration.create(800, TimeUnit.MILLISECONDS),
          Duration.create(2400, TimeUnit.MILLISECONDS))(searchuserFun)
   deletefriend=context.system.scheduler.schedule(Duration.create(900, TimeUnit.MILLISECONDS),
          Duration.create(10000, TimeUnit.MILLISECONDS))(deletefriendFun)
   addprofilepic = context.system.scheduler.schedule(Duration.create(1000, TimeUnit.MILLISECONDS),
          Duration.create(6000, TimeUnit.MILLISECONDS))(addprofilepicFun)
   viewprofilepic = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(1600, TimeUnit.MILLISECONDS))(viewprofilepicFun)
   viewprofile= context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(2800, TimeUnit.MILLISECONDS))(viewprofileFun)
   createalbum = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(3200, TimeUnit.MILLISECONDS))(createalbumFun)
   addpicstoalbum = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(3400, TimeUnit.MILLISECONDS))(addpicstoalbumFun)
   viewalbumpics = context.system.scheduler.schedule(Duration.create(1500, TimeUnit.MILLISECONDS),
          Duration.create(4400, TimeUnit.MILLISECONDS))(viewalbumpicsFun)
    case 5=>
     addfriend = context.system.scheduler.schedule(Duration.create(200, TimeUnit.MILLISECONDS),
          Duration.create(8000, TimeUnit.MILLISECONDS))(addFriendfun)
    viewpage = context.system.scheduler.schedule(Duration.create(300, TimeUnit.MILLISECONDS),
          Duration.create(4000, TimeUnit.MILLISECONDS))(viewpageFun)
    createpost =context.system.scheduler.schedule(Duration.create(400, TimeUnit.MILLISECONDS),
          Duration.create(6000, TimeUnit.MILLISECONDS))(createpostFun)
    viewpost = context.system.scheduler.schedule(Duration.create(500, TimeUnit.MILLISECONDS),
          Duration.create(2000, TimeUnit.MILLISECONDS))(viewpostFun)
   viewfriendlist=context.system.scheduler.schedule(Duration.create(600, TimeUnit.MILLISECONDS),
          Duration.create(7200, TimeUnit.MILLISECONDS))(viewfriendlistFun)
    searchuser=context.system.scheduler.schedule(Duration.create(800, TimeUnit.MILLISECONDS),
          Duration.create(4800, TimeUnit.MILLISECONDS))(searchuserFun)
   deletefriend=context.system.scheduler.schedule(Duration.create(900, TimeUnit.MILLISECONDS),
          Duration.create(10000, TimeUnit.MILLISECONDS))(deletefriendFun)
   addprofilepic = context.system.scheduler.schedule(Duration.create(1000, TimeUnit.MILLISECONDS),
          Duration.create(12000, TimeUnit.MILLISECONDS))(addprofilepicFun)
   viewprofilepic = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(3200, TimeUnit.MILLISECONDS))(viewprofilepicFun)
   viewprofile= context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(5600, TimeUnit.MILLISECONDS))(viewprofileFun)
   createalbum = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(6400, TimeUnit.MILLISECONDS))(createalbumFun)
   addpicstoalbum = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(6800, TimeUnit.MILLISECONDS))(addpicstoalbumFun)
   viewalbumpics = context.system.scheduler.schedule(Duration.create(1500, TimeUnit.MILLISECONDS),
    Duration.create(8800, TimeUnit.MILLISECONDS))(viewalbumpicsFun)*/
}
case notification(createdby :Int) =>
    //  println("From notification")
     // val result = pipeline(Get(Global.ViewPost + userid+"&personid="+createdby ))
     // displayResult(result)
     case updateencryptedkey(userid:Int, friendid:Int)=>
     setencryptedkey(userid,friendid)
     case UserStop =>
     println("User Stopped ")
     addfriend.cancel
     viewpage.cancel
     createpost.cancel
     viewpost.cancel
     viewfriendlist.cancel
     searchuser.cancel
     deletefriend.cancel
     addprofilepic.cancel
     viewprofile.cancel
     createalbum.cancel
     viewprofilepic.cancel
     viewalbumpics.cancel
     hideposts.cancel
   }

 /*  def loginFun(){
    
   }*/
   def addFriendfun() {
    if (countfriend < Myfriend) {
     countfriend = countfriend+1
   //  println("addFriendfun",userid)

  //   println("addFriendfun"+symkey)
      //println("Add Friend Request")
      val toadd = Random.nextInt(maxUser)
      if(toadd != userid && (friendset add toadd)){
        val result = pipeline(Post(Global.AddFriend + userid + "&friendid=" + toadd))
        setencryptedkey(userid,toadd)
        val notifyTo = "akka://default/user/"+toadd
        context.actorSelection(notifyTo) ! updateencryptedkey(toadd:Int, userid :Int)
      //var key2 = getPublickey(friendid,userid)
     // displayResult(result)
   }
   } else {
     addfriend.cancel
   }
 }

 def setencryptedkey(userid :Int, friendid:Int){ 
  var key = getPublickey(userid, friendid)
  /*  println("setencryptedkey",key)
    var enckey = encryption.encryptRSA(symkey,key)
  //  println("setencryptedkey" , enckey)
    val result = pipeline(Post(Global.SetEncryptkey + userid  + "&personid=" + friendid +"&encryptedkey="+ enckey))*/
  }
  def getPublickey(userid:Int, friendid:Int):PublicKey={
    val result = pipeline(Get(Global.GetPublicKey + userid + "&personid=" + friendid)) //deserialize to get publickey
    var res: String=""
    var key:PublicKey = null
    result.foreach{
      response=>
      res =(s"${response.entity.asString}")
      res =  res.replaceAll(" ","+")
      if(res.toLowerCase().contains("Error".toLowerCase()) == false)
      {
   //    println(" getPublickey",res)
       key = encryption.getpubkeyfromstring(res)
      // println("setencryptedkey",key)
      var enckey = encryption.encryptRSA(symkey,key)
    //  println("setencryptedkey" , enckey)
      if(enckey.endsWith("="))
      {
       enckey = enckey.substring(0,enckey.length() - 1)+"+";
     }
     val result = pipeline(Post(Global.SetEncryptkey + userid  + "&personid=" + friendid +"&encryptedkey="+ enckey))
     displayResult(result)
   }
 }
 key
}
def createpostFun() {
  if (countpost < Mypost) {
   countpost = countpost+1
     // println("createpostFun ")
     val topost = Global.postlist(Random.nextInt(Global.postlist.length))
     val res=topost.replace(' ','+')
      
   val my_keyAES =  encryption.generateAESkey
   val my_symkey = keyAES.replaceAll("=","")

     val iv = generateIv(countpost+"")
     val encpost  =  encryption.encryptWithAESKey(res,my_symkey,iv)
     if(userid == 1){
   //   println("Creatingpost")
   //   println(encpost)
    }
    var hiddenId = 1
    if(friendset.size > 0)
    {
      hiddenId = friendset.toList(Random.nextInt(friendset.size))
      /*create  List sym key encrypted with pubkey friend*/
       // val notifyTo = "akka://default/user/"+getid
       var encyptedlist =  new ListBuffer[String]()
       var enckey :String = ""
       for(i<-1 until friendset.size)
       {
          val fienPubkey = Global.Pubkeymap(friendset.toList(i))
        // println("Create Post Friend ID "+fienPubkey)
          enckey = encryption.encryptRSA(my_symkey,fienPubkey)
          val updateenclist = friendset.toList(i).toString + ","+enckey
        // println("Create Post "+updateenclist)
          encyptedlist+=  updateenclist  
       }
       var requestParam =  new CreatePostParam(userid,
        encpost,
        countpost,
        hiddenId,
        encyptedlist
        )
       var encodedString = URLEncoder.encode(requestParam.toJson+"","UTF-8")

       val result = pipeline(Post(Global.CreatePost+encodedString))

       // val result = pipeline(Post(Global.CreatePost + userid +"&post=" + encpost+"&postid=" +count+"&hidden=" +hiddenId))
      //  println(notifyTo)
      //val server = system.actorSelection(connectTo)
     // context.actorSelection(notifyTo) ! notification(userid)
   }
     //displayResult(result)
     } else {
       createpost.cancel
     }
   }


   def viewprofileFun() {
    //  println("viewpostFun ")
    val viewid = Random.nextInt(maxUser)
    val result = pipeline(Get(Global.ViewProfile + userid+"&personid="+viewid ))
   //   displayResult(result)
 }
 def searchuserFun() { 
     // println("searchuserFun ")
     val searched = Random.nextInt(Global.namelist.length);
     val searchstring = "&firstname="+Global.namelist(searched) + "&lastname=" + Global.lnamelist(searched)
     val result = pipeline(Get(Global.SearchUser + userid +searchstring))
      //displayResult(result)
    }
    def deletefriendFun() {
      //println("deletefriendFun ")
      /*val todel = friendset.toList(Random.nextInt(friendset.size))
      val result = pipeline(Post(DeleteFriend + userid +"&friendid" + todel))
      friendset -= todel*/
    // displayResult(result)
  }
  def hidepostFun() {
    if(countpost > 0){
    val postid = Random.nextInt(countpost)  
    if(friendset.size > 0)
    {
      val blockid = friendset.toList(Random.nextInt(friendset.size))
      val result = pipeline(Get(Global.HidePost + userid +"&personid="+blockid +"&postid="+postid))
    }
  }
  }
  def viewpostFun() {
      //println("viewpostFun ")
      if(friendset.size > 0){
      val viewid =  friendset.toList(Random.nextInt(friendset.size))//Random.nextInt(maxUser)
     // val result = pipeline(Get(Global.ViewPost + userid+"&personid="+viewid ))
     val result = pipeline(Get(Global.GetEncryptKey + userid+"&personid="+viewid ))
     var res: String=""
     result.foreach{
      response=>
      res =(s"${response.entity.asString}")
      if(res.toLowerCase().contains("Error".toLowerCase()) == false) {
        res = res.replaceAll(" ", "+")
        if (res.endsWith("+")) {
          res = res.substring(0, res.length() - 1) + "=";
        }
       // println("viewpostFun", res)

        val symmkey = findsymkey(res)
        var pipe: HttpRequest => Future[HttpResponse] = sendReceive //~> unmarshal[String]
        val request = HttpRequest(method = GET, uri = Global.ViewPost + userid + "&personid=" + viewid)
        val responseFuture: Future[HttpResponse] = pipe(request)
      //  println("viewpostFun outside ")
        responseFuture onComplete {
          case Success(result) =>
        //    println("viewpostFun case success ")
            var resp = result.entity.data.asString.parseJson.convertTo[List[ViewPostResponse]]
            var iter = resp.iterator
            while (iter.hasNext) {
              var message = iter.next()

                var temp = message.toString
                val splitString: Array[java.lang.String] = temp.split("\\,")
                val splitString2: Array[java.lang.String] = splitString(0).split("\\(")
                //println("splitString 0 "+splitString(0) )
                //println("splitString 1 "+splitString(1) )
                //println("splitString 2 "+splitString(2) )
                var postid = splitString2(1).toInt
                var encmsg = splitString(1)               
                encmsg = encmsg.replaceAll(" ", "+")
                 var symkey1 = splitString(2)
                //println(" viewpostFun symkey1 " + symkey1)
                symkey1 = symkey1.replaceAll(" ", "+")
                //println(" viewpostFun symkey1 after" + symkey1)
                var frn_key = findsymkey(symkey1)
                //println("\nPOST ID: " + postid)
                println("\n Encoded Post: " + encmsg)
                var post = finddata(encmsg, frn_key, postid)
                post = post.replace('+', ' ')
                println("Post: " + post)
                /* for (  i<-0 to splitString.length)
                 println("viewpostFun splitString["+i+"] is " + splitString[i]);
                 }*/


            }
          case Failure(error) =>
            println("Account Creation: " + error)
        }
       // println("viewpostFun ON Failure")
      }
      //val key = getEncryptkey(userid , viewid)
    }
    //  val data = finddata( "acb",key,"1") //to get postid and post
    // displayResult(result)
  }
}
  def viewfriendlistFun() {
      //println("viewfriendlistFun ")
      val viewid = Random.nextInt(maxUser)
      val result = pipeline(Get(Global.ViewFriendlist + userid +"&personid="+viewid))
     //displayResult(result)
   }

   def viewpageFun() {
       //println("View Page Request")
       val searchid = Random.nextInt(maxUser)
       val result = pipeline(Get(Global.ViewPage + userid+"&personid="+ searchid))
       if(userid == 1)
       displayResult(result)
     }

    //Image   Handling

    def addprofilepicFun() {
    //   println("addprofilepicFunc")
    val bytes = new Array[Byte](10)
    Random.nextBytes(bytes)
    val res = bytes.mkString(" ")
    val repl  = res.replace(' ','+')
    val result = pipeline(Post(Global.AddProfilePic + userid+"&pic="+ repl))
  //    displayResult(result)
}

def viewprofilepicFun() { 
     // println("searchuserFun ")
     val searched = Random.nextInt(maxUser);
     val result = pipeline(Get(Global.ViewProfilePic + userid +"&personid="+userid))
     //displayResult(result)
   }

   def createalbumFun() {
    if (countalbum < Myalbum) {
     countalbum = countalbum+1
    // println("createalbum")
    val toadd = 0 //Random.nextInt(Myalbum);
    val result = pipeline(Post(Global.CreateAlbum + userid + "&albumid=" + toadd))
   //   displayResult(result)
 }
 else
 createalbum.cancel
}

    def addpicstoalbumFun(){
    
  val albumid =  0 //Random.nextInt(Myalbum)
  val bytes = new Array[Byte](10)
  Random.nextBytes(bytes)
  val res = bytes.mkString(" ")
  val repl  = res.replace(' ','+')
  val iv = generateIv(countpic+"")
  val encpic  =  encryption.encryptWithAESKey(repl,symkey,iv)
   var hiddenId = 1
    if(friendset.size > 0)
    {

       //println("addpicstoalbumFun")
      hiddenId = friendset.toList(Random.nextInt(friendset.size))
       // val notifyTo = "akka://default/user/"+getid
       var requestParam =  new AlbumPicParam(userid,
        albumid,
        encpic,
        countpic,
         hiddenId
        )
       var encodedString = URLEncoder.encode(requestParam.toJson+"","UTF-8")
       val result = pipeline(Post(Global.AddPicstoAlbum+encodedString))
     
     countpic = countpic +1
     displayResult(result)
  }
}
  def viewalbumpicsFun() {
   //   println("viewalbumpicsFun ")
   val albumid = 0 //Random.nextInt(Myalbum)
   if(friendset.size > 0){
    val viewid = friendset.toList(Random.nextInt(friendset.size))
   //val result = pipeline(Get(Global.ViewAlbumPics + userid+"&personid="+personid+"&albumid="+albumid ))
    val result = pipeline(Get(Global.GetEncryptKey + userid+"&personid="+viewid ))  //get sym key
     var res: String=""
     result.foreach{
      response=>
      res =(s"${response.entity.asString}")
      if(res.toLowerCase().contains("Error".toLowerCase()) == false) {
        res = res.replaceAll(" ", "+")
        if (res.endsWith("+")) {
          res = res.substring(0, res.length() - 1) + "=";
        }
       // println("viewalbumpicsFun", res)

        val symmkey = findsymkey(res)
        var pipe: HttpRequest => Future[HttpResponse] = sendReceive //~> unmarshal[String]
        val request = HttpRequest(method = GET, uri = Global.ViewAlbumPics + userid + "&personid=" + viewid+ "&albumid="+albumid)
        val responseFuture: Future[HttpResponse] = pipe(request)
        responseFuture onComplete {
          case Success(result) =>
            var resp = result.entity.data.asString.parseJson.convertTo[List[ViewPhotoResponse]]
            var iter = resp.iterator
            while (iter.hasNext) {
              var message = iter.next()
              if (viewid == 1) {
                var temp = message.toString
                val splitString: Array[java.lang.String] = temp.split("\\,")
                val splitString2: Array[java.lang.String] = splitString(0).split("\\(")
                var postid = splitString2(1).toInt
                var encmsg = splitString(1)
                encmsg = encmsg.replaceAll(" ", "+")
           //     println("\nPOST ID: " + postid)
              //  println("\n Encoded Pic: " + encmsg)
                var post = finddata(encmsg, symmkey, postid)
                post = post.replace('+', ' ')
             //   println("Pic: " + post)
                /* for (  i<-0 to splitString.length)
                 println("viewpostFun splitString["+i+"] is " + splitString[i]);
                 }*/

              }
            }
          case Failure(error) =>
            println("Account Creation: " + error)
        }
      }
      //val key = getEncryptkey(userid , viewid)
    }
 }
   //  displayResult(result)
 }

   def generateIv(inpStr:String):Array[Byte]={
      val hashval = MessageDigest.getInstance("MD5").digest(inpStr.toString.getBytes("UTF-8"))//.map("%02x".format(_))
      hashval
    }
    def findsymkey(encrypt:String):String={ // Return sym key 
      encryption.decryptRSA(encrypt,keypair.getPrivate)
    }
    def finddata(inpudata:String , symkey:String , postid:Int):String={
      val iv = generateIv(postid+"")
      encryption.decryptWithAESKey(inpudata,symkey,iv)
    }

    def displayResult(result: scala.concurrent.Future[spray.http.HttpResponse]) {
      result.foreach {
        response =>
      //  println(Global.PageViewCount)
   //   println(s"Request completed with status ${response.status} and content:\n${response.entity.asString}")
    }

  }
}
