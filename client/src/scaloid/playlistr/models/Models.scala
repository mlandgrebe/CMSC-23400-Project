package scaloid.playlistr.models

import argonaut.Argonaut._
import argonaut.{Json, EncodeJson, CodecJson}
import scala.language.postfixOps


/**
 * We need this so that the types work out in our parser
 */
object Models {
  implicit def UserCodecJson: CodecJson[User] =
      casecodec3(User.apply, User.unapply)("userId", "uri", "name")

  private def encodeTo[A, B](op : Json => B, target: A)(implicit encode : EncodeJson[A]) =
    op(encode(target))

  sealed trait Model {
    def toParam: (String, String)
  }

  class SongRoom(id: Int) extends Model {
    override def toParam = ("songRoomId", id toString)


  }

  case class User(id: Int, uri: String, name: String) extends Model {
    override def toParam = ("userId", id toString)
//
//    private def encodeTo[A](op : Json => A)(implicit encode : EncodeJson[User]) =
//        op(encode(this))

    val encode = encodeTo(_.nospaces, this)
  }
}

