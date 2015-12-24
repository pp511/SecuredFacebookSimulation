package Server
import scala.collection.mutable
import Array._
import scala.collection.mutable.SynchronizedBuffer
import scala.collection.mutable.SynchronizedMap

class PostClass(post : String, postid : Int){
  var m_post = post
  var m_postid = postid
  var m_hiddenlist = new mutable.ArrayBuffer[Int]() with SynchronizedBuffer[Int]
  var encryptedkeymap = new mutable.HashMap[Int, String]() with SynchronizedMap[Int,String]
}

class Photo(pic :String) {
  var m_photo = pic
  var m_hiddenlist = new mutable.ArrayBuffer[Int]() with SynchronizedBuffer[Int]
}

class Album(albumId : Int) {
  var picmap = new mutable.HashMap[Int,Photo]
 // var piclist = new mutable.ArrayBuffer[Photo]() with SynchronizedBuffer[Photo]
  val albumid = albumId
}




/*
class AlbumPicResponse(picid : Int, pic : String){

}
*/

class User(userId : Int,loginId : String, passWd : String, firstName : String ,lastName  : String ,Gender : String ,Country : String ,City : String ,Profession : String ,Interestedin : String, Publickey : String) {
  var friendlist = new mutable.ArrayBuffer[Friend]() with SynchronizedBuffer[Friend]
  var friendidlist = new mutable.ArrayBuffer[Int]() with SynchronizedBuffer[Int]

 // var postlist = new mutable.ArrayBuffer[PostClass]() with SynchronizedBuffer[PostClass]

   var postmap =  new mutable.HashMap[Int, PostClass]() with SynchronizedMap[Int,PostClass]


  var albummap = new mutable.HashMap[Int,Album]

  var albumlist = new mutable.ArrayBuffer[Album]
  var albumidlist = new mutable.ArrayBuffer[Int]

//  var profilepic =  new Array[Byte](50)
  var profilepic = ""
  val userid = userId
  val loginid = loginId
  val passwd = passWd
  val gender = Gender
  val firstname = firstName
  val lastname = lastName
  var country = Country
  var city = City
  var profession = Profession
  var interestedin = Interestedin

  val publickey =   Publickey

  var encryptedkeymap = new mutable.HashMap[Int, String]() with SynchronizedMap[Int,String]
  

  var securerandomnumber : String  = ""


  /* Intrinsic values*/
   var lastpostid = 0;
}

