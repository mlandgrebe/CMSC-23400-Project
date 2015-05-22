package scaloid.playlistr.remote

import argonaut.Parse
import scaloid.playlistr.models.Models.{UnitResponse, Receivable, User}
import scala.language.postfixOps
import scalaz.\/
import dispatch.Future

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * We need to name this APIRequest so it doesn't conflict with things that Dispatch uses
 */
object APIRequest {
  sealed trait APIRequest {
    val params: Map[String, String]
    type ResponseType <: Receivable
    // this means that we can rely on toString to resolve to our endpoint names
    override def toString = super.toString.toLowerCase

    protected def parseResponse(string: String): \/[String, ResponseType]

    def submit(implicit server: Server): Future[\/[String, ResponseType]] =
      for (res <- server.submit(this)) yield {
        res.flatMap(parseResponse)
      }

  }

  case class Create(host: User, name: String) extends  APIRequest {
    val params = List(host toParam, ("name", name)) toMap
    override type ResponseType = UnitResponse

    override protected def parseResponse(string: String) =
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

