package scaloid.playlistr.remote

import argonaut.Argonaut._
import argonaut.{HCursor, CodecJson}
import argonaut._
/**
 * Created by patrick on 5/11/15.
 */
object APIResponse {

  implicit def UnitResponseCodecJson: CodecJson[UnitResponse] =
    CodecJson(
      (u: UnitResponse) =>
        ("status" := u.status) ->: jEmptyObject,
      (c: HCursor) => for {
        // FIXME: Check this status is ok
        status <- (c --\ "status").as[String]
      } yield UnitResponse()
    )


  sealed trait APIResponseType

  // The empty response -- all we care about was the status
  case class UnitResponse() extends APIResponseType{
    val status = "OK"
  }
}