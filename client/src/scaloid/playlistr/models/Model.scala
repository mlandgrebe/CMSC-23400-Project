package scaloid.playlistr.models

import argonaut.Argonaut._
import argonaut.CodecJson


/**
 * We need this so that the types work out in our parser
 */
object Models {
  implicit def UserCodecJson: CodecJson[User] = casecodec3(User.apply, User.unapply)("id", "uri", "name")

  sealed trait Model {
    def toParam: (String, String)
  }

  class SongRoom(id: Int) extends Model {
    override def toParam = ("songRoomId", id toString)
  }

  case class User(id: Int, uri: String, name: String) extends Model {
    override  def toParam = ("userId", id toString)
  }
}

