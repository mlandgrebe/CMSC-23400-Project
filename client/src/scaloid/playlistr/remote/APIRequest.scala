package scaloid.playlistr.remote

import argonaut.{CodecJson, CodecJsons, DecodeJson, Parse}
import scaloid.playlistr.models.Models.{SpotifyURI, UnitResponse, Receivable, User}
import scala.language.postfixOps
import scalaz.\/
import dispatch.Future
import scaloid.playlistr.models.Models.{UserCodecJson, UnitResponseCodecJson, SongRoomCodecJson, SpotifyURICodecJson}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * We need to name this APIRequest so it doesn't conflict with things that Dispatch uses
 */
object APIRequest {
  sealed trait APIRequest {
    val params: Map[String, String]
    type ResponseType <: Receivable
    // this means that we can rely on toString to resolve to our endpoint names
    def toEndpoint = super.toString.toLowerCase

    protected def parseResponseWith(decoder: DecodeJson[ResponseType])
                                   (string: String): \/[String, ResponseType] =
      Parse.decodeEither(string)(decoder)
    protected val parseResponse: String => \/[String, ResponseType]
//      Parse.decodeEither[ResponseType](string)

    def submit(implicit server: Server): Future[\/[String, ResponseType]] =
      for (res <- server.submit(this)) yield {
        res.flatMap(parseResponse)
      }

  }

  case class Create(host: User, name: String) extends  APIRequest {
    val params = List(host toParam, ("name", name)) toMap
    override type ResponseType = UnitResponse

    override protected val parseResponse = parseResponseWith(UnitResponseCodecJson) _
  }

  case class Login(uri: SpotifyURI) extends APIRequest {
    val params = List(uri toParam) toMap
    override type ResponseType = User

    override protected val parseResponse = parseResponseWith(UserCodecJson) _
//    override protected def parseResponse(string: String) =
//      Parse.decodeEither[ResponseType](string)
  }

  case class Register(user: User) extends APIRequest {
    val params = List((user toParam)) toMap
    override type ResponseType = UnitResponse

    override protected val parseResponse = parseResponseWith(UnitResponseCodecJson) _
  }
}



//case class Join(songRoom: SongRoom, user: User) extends Request {
//  val params = List(songRoom toParam, user toParam) toMap
//}
//
//case class Leave(songRoom: SongRoom, user: User) extends Request {
//  val params = List(songRoom toParam, user toParam) toMap
//}

