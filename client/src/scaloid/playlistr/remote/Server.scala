package scaloid.playlistr.remote

import argonaut.{DecodeJson, Parse}
import dispatch._
import scaloid.playlistr.models.Models.{User, Model}
import com.ning.http.client.Request
import scaloid.playlistr.remote.APIRequest.{Create, APIRequest}
import scaloid.playlistr.remote.APIResponse.{UnitResponse, APIResponseType}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.{-\/, \/}

/**
 * In a better world, some of these things will be read
 * from a configuration file.
 */
object Server {
  val defaultBaseUrl = host("localhost")
  val defaultPort = 8080
}

class Server(hostUrl : Req = Server.defaultBaseUrl / Server.defaultPort) {

  // We're going to rely on case class's toString to to the Right Thing
  private def parseRequest (request: APIRequest) : (Request, OkFunctionHandler[String]) = {
    (hostUrl / request.toString << request.params) OK as.String
  }

  // We want a function that goes from Either[Throwable, String] -> \/[String, String] to
  // play nice with scalaz

  def submit[A <: APIRequest](request: A): Future[\/[String, APIResponseType]] =
    for(res <- Http(parseRequest(request)).either) yield res.F
  >>= request.parseResponse
//      res match {
//        case Left(exc) => Left("Connection error: " + exc.getMessage)
//        case Right(str) => request parseResponse str
//      }
}
