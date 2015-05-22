package scaloid.playlistr.models

import argonaut.Argonaut._
import argonaut.{HCursor, Json, EncodeJson, CodecJson}
import scala.language.postfixOps


/**
 * We need this so that the types work out in our parser
 */
object Models {

  implicit def UserCodecJson: CodecJson[User] =
      casecodec3(User.apply, User.unapply)("userId", "spotifyURI", "name")

  implicit def SongRoomCodecJson: CodecJson[SongRoom] =
    casecodec1(SongRoom.apply, SongRoom.unapply)("songRoomId")

  implicit def SpotifyURICodecJson: CodecJson[SpotifyURI] =
    casecodec1(SpotifyURI.apply, SpotifyURI.unapply)("uri")

  implicit def UnitResponseCodecJson: CodecJson[UnitResponse] =
    CodecJson(
      (u: UnitResponse) =>
        ("status" := u.status) ->: jEmptyObject,
      (c: HCursor) => for {
        status <- (c --\ "status").as[String]
      } yield UnitResponse(status)
    )

  private def encodeTo[A, B](op : Json => B, target: A)(implicit encode : EncodeJson[A]) =
    op(encode(target))

  sealed trait Receivable

  case class UnitResponse(status: String) extends Receivable

  sealed trait Sendable extends Receivable {
    def toParam: (String, String)
    def encode: String
  }

  case class SongRoom(id: Int) extends Sendable {
    override def toParam = ("songRoomId", id toString)
    override def encode = encodeTo(_.nospaces, this)
  }

  case class SpotifyURI(uri: String) extends Sendable {
    override def toParam = ("uri", uri)
    override def encode = encodeTo(_.nospaces, this)
  }

  case class User(id: Int, uri: SpotifyURI, name: String) extends Sendable {
    override def toParam = ("userId", id toString)
    override def encode = encodeTo(_.nospaces, this)
  }
}

