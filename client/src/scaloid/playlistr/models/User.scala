package scaloid.playlistr.models

/**
 * Created by patrick on 5/9/15.
 */
class User(userId: Int) extends Model {

  def toParam = ("userId", userId toString)
}
