//package scaloid.playlistr.remote
//
//import argonaut.Argonaut._
//import argonaut.{HCursor, CodecJson}
//import argonaut._
//import scaloid.playlistr.models.Models.User
//
///**
// * Created by patrick on 5/11/15.
// */
//object APIResponse {
//
//
//
//
//  sealed trait APIResponseType
//
//  // The empty response -- all we care about was the status
//  case class UnitResponse(status: String) extends APIResponseType
//
//  case class ModelResponse()
//}