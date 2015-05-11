package scaloid.playlistr.remote

/**
 * Created by patrick on 5/11/15.
 */
object APIResponse {

  sealed trait APIResponseType

  // The empty response -- all we care about was the status
  case class UnitResponse() extends APIResponseType
}