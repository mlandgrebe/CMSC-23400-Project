package scaloid.playlistr.remote

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.spotify.sdk.android.authentication.{AuthenticationRequest, AuthenticationResponse, AuthenticationClient}
import com.spotify.sdk.android.player.PlayerNotificationCallback.{ErrorType, EventType}
import com.spotify.sdk.android.player._
import org.scaloid.common.LoggerTag

/**
 * Created by patrick on 5/22/15.
 */
object Authentication {
  val CLIENT_ID = "2de62f40903247208d3dd5e91846c410"
  val REDIRECT_URI = "attuapp://callback"
  val REQUEST_CODE = 1337



  val builder = {
    val _builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
    _builder.setScopes(Array("user-read-private", "streaming"))
    _builder
  }

  def getRequest() = builder.build()

  def correctRequest(requestCode: Int) = requestCode == REQUEST_CODE

  def authenticateInActivity(activity: Activity) = AuthenticationClient.openLoginActivity(
    activity, REQUEST_CODE, getRequest())

}

//class Authentication extends  ConnectionStateCallback {
//
//
//
//
//  override def onPlaybackEvent(eventType: EventType, playerState: PlayerState) {
//    Log.d("MainActivity", s"Playback event received: ${eventType.name()}")
//    eventType match {
//      case _ => //break
//    }
//  }
//
//  override def onPlaybackError(errorType: ErrorType, errorDetails: String) {
//    Log.d("MainActivity", "Playback error received: " + errorType.name())
//    errorType match {
//      case _ => //break
//    }
//  }
//
//}
