package scaloid.playlistr.remote

import dispatch.Http
import org.scalamock.proxy.ProxyMockFactory
import scaloid.playlistr.BaseTest
import scaloid.playlistr.models.Models.{SongRoom, User}

/**
 * Created by patrick on 5/10/15.
 */
class ServerTest extends BaseTest with ProxyMockFactory {
  val testUser = User(22, "uri", "John")
  val testRequest = Create(testUser, "thisRoom")

  "The server" should "make a call to the API" in {
    val api = mock[Http]

    Server.submit[SongRoom](testRequest)

    (stub _).verify('testRequest)

  }
}
