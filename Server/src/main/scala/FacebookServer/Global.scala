package Server
import scala.collection.mutable
import scala.collection.mutable.SynchronizedBuffer
import scala.collection.mutable.SynchronizedMap

import Server._
object Global {
  var usermap = new mutable.HashMap[Int, User]() with SynchronizedMap[Int,User]
  var useridlist = new mutable.ArrayBuffer[Int]() with SynchronizedBuffer[Int]
}

