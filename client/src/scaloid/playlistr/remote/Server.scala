package scaloid.playlistr.remote

import argonaut.{DecodeJson, Parse}
import dispatch._
import scaloid.playlistr.models.Models.{User, Model}
import com.ning.http.client.{AsyncHandler, Request}
import scaloid.playlistr.remote.APIRequest.{Create, APIRequest}
import scaloid.playlistr.remote.APIResponse.{UnitResponse, APIResponseType}
import scaloid.playlistr.remote.Server.RequestMaker

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.{-\/, \/}
import scalaz.syntax.std.either._


/**
 * In a better world, some of these things will be read
 * from a configuration file.
 */
object Server {
  val defaultHostUrl = host("localhost") / 8080

  type ParsedRequest = (Request, OkFunctionHandler[String])
  type RequestMaker = (ParsedRequest) => Future[String]

  def apply(hostUrl : Req = defaultHostUrl, requestMaker: RequestMaker = Http.apply(_:ParsedRequest))
            (implicit ec: ExecutionContext) = new Server(hostUrl, requestMaker, ec)


}

class Server(hostUrl : Req, reqMaker: RequestMaker, executionContext: ExecutionContext) {

  // We're going to rely on case class's toString to to the Right Thing
  private def parseRequest (request: APIRequest) : (Request, OkFunctionHandler[String]) = {
    (hostUrl / request.toString << request.params) OK as.String
  }


  // We want a function that goes from Either[Throwable, String] -> \/[String, String] to
  // play nice with scalaz

  def submit[A <: APIRequest](request: A): Future[\/[String, String]] =
    for(res <- reqMaker(parseRequest(request)).either) yield
        res.disjunction.leftMap(_.getMessage)
}
