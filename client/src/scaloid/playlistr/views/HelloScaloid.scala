package scaloid.playlistr.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.spotify.sdk.android.authentication.{AuthenticationClient, AuthenticationRequest, AuthenticationResponse}
import com.spotify.sdk.android.player.PlayerNotificationCallback.{ErrorType, EventType}
import com.spotify.sdk.android.player.{Config, ConnectionStateCallback, Player, PlayerNotificationCallback, PlayerState, Spotify}
import scaloid.playlistr.views.HelloScaloid._

object HelloScaloid {

  private val CLIENT_ID = "2de62f40903247208d3dd5e91846c410"

  private val REDIRECT_URI = "attuapp://callback"

  private val REQUEST_CODE = 1337
}

class HelloScaloid extends Activity with PlayerNotificationCallback with ConnectionStateCallback {

  private var mPlayer: Player = _

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    //setContentView(android.R.layout.activity_main)

    val builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
    builder.setScopes(Array("user-read-private", "streaming"))
    val request = builder.build()
    AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)
  }

  protected override def onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
    super.onActivityResult(requestCode, resultCode, intent)
    if (requestCode == REQUEST_CODE) {
      val response = AuthenticationClient.getResponse(resultCode, intent)
      if (response.getType == AuthenticationResponse.Type.TOKEN) {
        val playerConfig = new Config(this, response.getAccessToken, CLIENT_ID)
        mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {

          override def onInitialized(player: Player) {
            mPlayer.addConnectionStateCallback(HelloScaloid.this)
            mPlayer.addPlayerNotificationCallback(HelloScaloid.this)
            mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V")
          }

          override def onError(throwable: Throwable) {
            Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage)
          }
        })
      }
    }
  }

  override def onLoggedIn() {
    Log.d("MainActivity", "User logged in")
  }

  override def onLoggedOut() {
    Log.d("MainActivity", "User logged out")
  }

  override def onLoginFailed(error: Throwable) {
    Log.d("MainActivity", s"Login failed because: $error")
  }

  override def onTemporaryError() {
    Log.d("MainActivity", "Temporary error occurred")
  }

  override def onConnectionMessage(message: String) {
    Log.d("MainActivity", s"Received connection message: $message")
  }

  override def onPlaybackEvent(eventType: EventType, playerState: PlayerState) {
    Log.d("MainActivity", s"Playback event received: ${eventType.name()}")
    eventType match {
      case _ => //break
    }
  }

  override def onPlaybackError(errorType: ErrorType, errorDetails: String) {
    Log.d("MainActivity", "Playback error received: " + errorType.name())
    errorType match {
      case _ => //break
    }
  }

  protected override def onDestroy() {
    Spotify.destroyPlayer(this)
    super.onDestroy()
  }
}