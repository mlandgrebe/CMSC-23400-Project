package scaloid.playlistr.remote

import argonaut.Parse
import scaloid.playlistr.models.Models.User
import scaloid.playlistr.remote.APIResponse.{UnitResponse, APIResponseType}
import scala.language.postfixOps
import scalaz.\/

/**
 * We need to name this APIRequest so it doesn't conflict with things that Dispatch uses
 */
object APIRequest {
  sealed trait APIRequest {
    val params: Map[String, String]

    // this means that we can rely on toString to resolve to our endpoint names
    override def toString = super.toString.toLowerCase

    def parseResponse(string: String): \/[String, APIResponseType]
  }

  case class Create(host: User, name: String) extends  APIRequest {
    val params = List(host toParam, ("name", name)) toMap

    override def parseResponse(string: String): \/[String, UnitResponse] =
      Parse.decodeEither[UnitResponse](string)
  }
}



//case class Join(songRoom: SongRoom, user: User) extends Request {
//  val params = List(songRoom toParam, user toParam) toMap
//}
//
//case class Leave(songRoom: SongRoom, user: User) extends Request {
//  val params = List(songRoom toParam, user toParam) toMap
//}

