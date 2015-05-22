package scaloid.playlistr.remote

import dispatch.StatusCode
import scala.language.implicitConversions

/**
 * Note: http://scalaz.github.io/scalaz/scalaz-2.9.1-6.0.4/doc.sxr/scalaz/http/response/Status.scala.html
 */
object StatusCodes {
  sealed trait ConvertibleStatus extends StatusCode

  object Unauthorized extends StatusCode(401) with ConvertibleStatus
  object Forbidden extends StatusCode(403) with ConvertibleStatus

  implicit def statusInt(status: ConvertibleStatus): Int = status.code
}
