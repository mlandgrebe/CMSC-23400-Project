package scaloid.playlistr.remote

import dispatch._
import com.ning.http.client.Request
import org.scalamock.scalatest.MockFactory
import scaloid.playlistr.BaseTest
import scaloid.playlistr.models.Models.{UnitResponse, SongRoom, User}
import scaloid.playlistr.remote.APIRequest.Create
import scaloid.playlistr.remote.Server.{ParsedRequest, RequestMaker}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scalaz.{-\/, \/-, \/}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by patrick on 5/10/15.
 */
class ServerTest extends BaseTest with MockFactory {
  val testUser = User(22, "uri", "John")
  val testRequest = Create(testUser, "thisRoom")
  val testHost = host("www.some.url")
  val testPort = "5555"
  val expectedRequest = (testHost / testPort / testRequest.toString) << testRequest.params

  "The server" should "make a call to the API" in {
    val mockReqMaker = mockFunction[ParsedRequest, Future[String]]
    implicit val server = Server(requestMaker = mockReqMaker)

    // Can't specify the request directly, but that's okay --- we should test it somewhere else
    mockReqMaker.expects(*).returning(Future.successful("{\"status\": \"OK\"}"))

    val res: \/[String, UnitResponse] = Await.result(testRequest.submit, 10000 millis)

    res match {
      case \/-(response) => response.status shouldBe "OK"
      case -\/(errMessage) => errMessage shouldBe "" // impossible
    }

  }
}
