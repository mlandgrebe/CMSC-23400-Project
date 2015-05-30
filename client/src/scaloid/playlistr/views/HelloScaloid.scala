package scaloid.playlistr.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.spotify.sdk.android.authentication.{AuthenticationClient, AuthenticationResponse}
import com.spotify.sdk.android.player.{ConnectionStateCallback, Config, Player, Spotify}
import dispatch._
import org.scaloid.common._
import scaloid.playlistr.remote.APIRequest.Login

import scala.concurrent.ExecutionContext.Implicits.global

import scaloid.playlistr.remote.Authentication

object HelloScaloid {
  implicit val loggerTag: LoggerTag = LoggerTag("Authentication")
}

class HelloScaloid extends SActivity with ConnectionStateCallback  {

  private var mPlayer: Player = _

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val google = url("google.com")
    val res = Http(google OK as.String)
    debug(res.print)

    Authentication.authenticateInActivity(this)
  }

  protected override def onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
    super.onActivityResult(requestCode, resultCode, intent)
    assert(Authentication.correctRequest(requestCode))

    val response = AuthenticationClient.getResponse(resultCode, intent)
    response.getType match {
      case AuthenticationResponse.Type.TOKEN =>

      case AuthenticationResponse.Type.ERROR => authFailureDiaglogue(response.getError)

//        val playerConfig = new Config(this, response.getAccessToken, CLIENT_ID)
//        mPlayer = Spotify.getPlayer(
//          playerConfig, this, new Player.InitializationObserver() {
//
//            override def onInitialized(player: Player) {
//              mPlayer.addConnectionStateCallback(HelloScaloid.this)
//              mPlayer.addPlayerNotificationCallback(HelloScaloid.this)
//              mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V")
//            }
//
//            override def onError(throwable: Throwable) {
//              Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage)
//            }
//          })
    }
  }

  def authFailureDiaglogue(message: String): Unit = {
    new AlertDialogBuilder("LOGIN FAILED!", "Reason: $message") {
      positiveButton("Exit", System.exit(1))
    }.show()
  }

  override def onLoggedIn(): Unit = {
    debug("User logged in")
  }

  override def onLoggedOut(): Unit = {
    debug("User logged out")
  }

  override def onLoginFailed(error: Throwable): Unit = {
    debug(s"Login failed because: $error")
    authFailureDiaglogue(error.toString)
  }

  override def onTemporaryError(): Unit = {
    debug("Temporary error occurred")
  }

  override def onConnectionMessage(message: String): Unit = {
    debug(s"Received connection message: $message")
  }


  override def onDestroy(): Unit = {
    Spotify.destroyPlayer(this)
    super.onDestroy()
  }
}