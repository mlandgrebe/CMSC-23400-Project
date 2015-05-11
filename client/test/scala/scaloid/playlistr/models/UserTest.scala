package scaloid.playlistr.models

import argonaut.{DecodeJson, EncodeJson, Parse}
import org.scalatest.FlatSpec
import org.scalatest._
import scaloid.playlistr.models.Models.User
import scala.language.postfixOps

/**
 * Created by patrick on 5/9/15.
 */
class UserTest extends FlatSpec with Matchers {
  val testId = 22
  val testURI = "uri"
  val testName = "John"
  val testUser = new User(testId, testURI, testName)
  val userJSON = s"""{"userId":$testId,"uri":"$testURI","name":"$testName"}"""



  "A user" should "know its parameters" in {
    println(testUser.toParam)
    println(("userId", testId))
    println(testUser.toParam == ("userId", testId))
    testUser.toParam should equal (("userId", testId toString))
  }

  it should "be decodable from JSON" in {
    val decoded: Option[User] = Parse.decodeOption[User](userJSON)(Models.UserCodecJson.Decoder)

    decoded should not be None
    decoded.get shouldBe testUser
  }

  it should "be encodable to JSON" in {
    val encoded = testUser.encode
    println(encoded)
  }

  it should "be be preserved by unencoding and re-encoding" in {
    val res = Parse.decodeOption[User](testUser.encode)

    res should not be None
    res.get shouldBe testUser
  }

}
