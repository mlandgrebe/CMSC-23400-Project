package scaloid.playlistr.models

import argonaut.CodecJson, argonaut.Argonaut._

/**
 * This is magic that gives us parsing for free
 */
object User {
  implicit def UserCodecJson: CodecJson[User] = casecodec3(User.apply, User.unapply)("id", "uri", "name")
}

case class User(id: Int, uri: String, name: String) extends Model {
  def toParam = ("userId", id toString)
}

