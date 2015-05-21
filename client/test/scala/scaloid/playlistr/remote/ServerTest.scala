package scaloid.playlistr.remote

import dispatch._
import com.ning.http.client.Request
import org.scalamock.scalatest.MockFactory
import scaloid.playlistr.BaseTest
import scaloid.playlistr.models.Models.{SongRoom, User}
import scaloid.playlistr.remote.APIRequest.Create
import scaloid.playlistr.remote.APIResponse.UnitResponse
import scaloid.playlistr.remote.Server.{ParsedRequest, RequestMaker}

import scala.concurrent.duration._
import scala.language.postfixOps
import scalaz.{\/-, \/}

/**
 * Created by patrick on 5/10/15.
 */
class ServerTest extends BaseTest with MockFactory {
  val testUser = User(22, "uri", "John")
  val testRequest = Create(testUser, "thisRoom")
  val testHost = host("www.some.url")
  val testPort = "5555"
  val expectedRequest = ((testHost / testPort / testRequest.toString) << testRequest.params) OK as.String



  "The server" should "make a call to the API" in {
    val mockReqMaker = mockFunction[ParsedRequest, Future[String]]
    val server = new Server(reqMaker = mockReqMaker)


    mockReqMaker.expects(expectedRequest).returning(Future.successful("{status: \"OK\"}"))

    val res = testRequest.submit.result(0 millis)

    res.isRight should be true

    res match {
      case \/-(response) => response.status should be "OK"
      case -\/ => false should be true
    }

  }
}
