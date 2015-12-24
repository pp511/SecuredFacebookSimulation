package Server

import spray.json.DefaultJsonProtocol

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val friendFormat = jsonFormat2(Friend)
  implicit val FriendListFormat = jsonFormat1(FriendList)
  implicit val personpageFormat = jsonFormat11(PersonPage)
  implicit val limitedprofileFormat = jsonFormat6(LimitedProfile)
  implicit val postresponseFormat = jsonFormat3(PostResponse)
  implicit val loginresultFormat = jsonFormat2(loginResult)
  implicit val photoresponseFormat = jsonFormat2(PhotoResponse)
}
case class Friend(firstname: String,lastname: String)
case class FriendList(frenlist : Seq[Friend])
case class PostResponse(id : Int, post : String , key : String)
case class loginResult(result: String , reason : String)
case class PhotoResponse(id :Int , photo : String)


case class PersonPage(
   loginid: String,
   firstname: String,
   lastname: String,
   gender: String,
   country: String,
   city: String,
   profession: String,
   interestedin : String,
   friendidlist: Seq[Friend],
   albumidlist : Seq[Int],
   profilepic : String
)
case class LimitedProfile(
    firstname: String,
    lastname: String,
    gender: String,
    country: String,
    city: String,
    profession : String
)
