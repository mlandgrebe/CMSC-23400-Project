package scaloid.playlistr.models

import argonaut.Parse
import org.scalatest.FlatSpec
import org.scalatest._
import scaloid.playlistr.models.Models.User

/**
 * Created by patrick on 5/9/15.
 */
class UserTest extends FlatSpec with Matchers {
  val testId = 22
  val testURI = "uri"
  val testName = "John"
  val testUser = new User(testId, testURI, testName)
  val userJSON = s"{userId=$testId, uri=$testURI, name=$testName"


  "A user" should "know its parameters" in {
    testUser.toParam should be ("userId", testId)
  }

  it should "be decodable from JSON" in {
    val decoded: Option[User] = Parse.decodeOption[User](userJSON)

    decoded should not be None
    decoded.get shouldBe testUser
  }
}
