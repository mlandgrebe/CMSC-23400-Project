package scaloid.playlistr.models

/**
 * Created by patrick on 5/9/15.
 */
object SongRoom {

}



class SongRoom(songRoomId: Int) extends Model {

  override def toParam = ("songRoomId", songRoomId toString)
}
