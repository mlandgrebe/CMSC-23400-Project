package scaloid.playlistr.models

import argonaut.Parse
import scaloid.playlistr.BaseTest
import scaloid.playlistr.models.Models.{SpotifyURI, User}

import scala.language.postfixOps

/**
 * Created by patrick on 5/9/15.
 */
class UserTest extends BaseTest {
  val testId = 22
  val testURI = SpotifyURI("uri")
  val testName = "John"
  val testUser = new User(testId, testURI, testName)
  val userJSON = s"""{"userId":$testId,"spotifyURI":{"uri":"${testURI.uri}"},"name":"$testName"}"""



  "A user" should "know its parameters" in {
    testUser.toParam should equal (("userId", testId toString))
  }

  it should "be decodable from JSON" in {
    val decoded: Option[User] = Parse.decodeOption[User](userJSON)(Models.UserCodecJson.Decoder)
    println(s"decoded is: $decoded")
    decoded should not be None
    decoded.get shouldBe testUser
  }

  it should "be encodable to JSON" in {
    val encoded: String = testUser.encode
    println(encoded)
    println(userJSON)

    encoded shouldBe userJSON
  }

  it should "be be preserved by unencoding and re-encoding" in {
    val res = Parse.decodeOption[User](testUser.encode)

    res should not be None
    res.get shouldBe testUser
  }

}
