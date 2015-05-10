package scaloid.playlistr.models

/**
 * Created by patrick on 5/9/15.
 */
abstract class Model {

  abstract def toParam: (String, String)
}
