package scaloid.playlistr.remote

import argonaut.Parse
import dispatch._
import scaloid.playlistr.models.Model

import scala.concurrent.Future

/**
 * Singleton to carry around some universal info about our sever. In a better world, some of these things will be read
 * from a configuration file.
 */
object Server {
  val baseUrl = host("localhost")
  val port = 8080

  def hostUrl = baseUrl / port

  // We're going to rely on case class's toString to to the Right Thing
  private def parseRequest (request: Request) : Req = {
    hostUrl / request.toString << request.params
  }

  def submit[M <: Model] (request: Request): Option[M] = {
    Http(parseRequest(request) OK as.String).completeOption.flatMap(Parse.decodeOption[M])
  }
}
