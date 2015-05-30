package scaloid.playlistr.remote

import dispatch._
import com.ning.http.client.Request
import org.scalamock.scalatest.MockFactory
import scaloid.playlistr.BaseTest
import scaloid.playlistr.models.Models.{SpotifyURI, UnitResponse, SongRoom, User}
import scaloid.playlistr.remote.APIRequest.{APIRequest, Login, Create}
import scaloid.playlistr.remote.Server.{ParsedRequest, RequestMaker}
import scaloid.playlistr.remote.StatusCodes.{Forbidden, Unauthorized}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scalaz.{-\/, \/-, \/}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by patrick on 5/10/15.
 */
class ServerTest extends BaseTest with MockFactory {
  val testURI = SpotifyURI("uri")
  val testUser = User(22, testURI, "John")
  val testHost = host("www.some.url")
  val testPort = "5555"
//  val expectedRequest = (testHost / testPort / testRequest.toString) << testRequest.params

  trait Fixture {
    val mockReqMaker = mockFunction[ParsedRequest, Future[String]]
    val server = Server(requestMaker = mockReqMaker)
    def serverSays(toSay: Future[String]): Unit =
      mockReqMaker.expects(*).returning(toSay)
  }

  private def getResult[B](reqResult: Future[\/[String, B]]): \/[String, B] =
    Await.result(reqResult, 10000 millis)

  "The server" should "let users make a room" in new Fixture {
    val testRequest = Create(testUser, "thisRoom")
    serverSays(Future.successful("{\"status\": \"OK\"}"))
    // Can't specify the request directly, but that's okay --- we should test it somewhere else

    inside(getResult(testRequest.submit(server)))  {
      case \/-(response) => response.status shouldBe "OK"
    }
  }

  it should "let known users log in" in new Fixture {
    val testRequest = Login(testURI)
    serverSays(Future.successful(testUser.encode))

    inside(getResult(testRequest.submit(server))) {
      case \/-(user) => user shouldBe testUser
    }
  }

  it should "not let unknown users log in" in new Fixture {
    val testRequest = Login(testURI)
    serverSays(Future.failed(new StatusCode(Forbidden)))

    inside(getResult(testRequest.submit(server))) {
      case -\/(code) => code shouldBe Forbidden.getMessage
    }
  }
}
