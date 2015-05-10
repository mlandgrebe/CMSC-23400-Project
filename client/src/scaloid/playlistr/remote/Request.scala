package scaloid.playlistr.remote

import scaloid.playlistr.models.{User, SongRoom}

/**
 * Created by patrick on 5/9/15.
 */
abstract class Request {
  abstract val params: Map[String, String]

}

case class Join(songRoom: SongRoom, user: User) extends Request {
  val params = List(songRoom toParam, user toParam) toMap
}

case class Leave(songRoom: SongRoom, user: User) extends Request {
  val params = List(songRoom toParam, user toParam) toMap
}

case class Create(host: User, name: String) extends  Request {
  val params = List(host toParam, ("name", name)) toMap
}