package controllers.error

import org.slf4j.LoggerFactory
import play.api.http.HttpErrorHandler
import play.api.mvc.Results.{InternalServerError, NotFound, Status, Unauthorized}
import play.api.mvc.{Request, RequestHeader, Result}

import javax.inject.Singleton
import scala.concurrent.Future

@Singleton
class ErrorHandler extends HttpErrorHandler {
  private val logger = LoggerFactory.getLogger(ErrorHandler.getClass)

  val errorData = Map(
    404 -> "Page not found",
    403 -> "You don't have enough rights",
    500 -> "Oops. Something went wrong on our side",
  )

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    var code = statusCode
    if (!errorData.contains(statusCode)) code = 404
    Future.successful {
      Status(statusCode)(views.html.error(statusCode, errorData(statusCode))(Request(request, None)))
    }
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    logger.error("Server error occurred: ", exception)
    Future.successful {
      val statusCode = 500
      Status(statusCode)(views.html.error(statusCode, errorData(statusCode))(Request(request, None)))
    }
  }
}

object ErrorHandler {
  def unauthorizedView(implicit r: Request[_]): Result =
    Unauthorized(views.html.error(403, "You don't have enough rights"))

  def notFoundView(implicit r: Request[_]): Result =
    NotFound(views.html.error(404, "Page not found"))

  def serverErrorView(implicit r: Request[_]): Result =
    InternalServerError(views.html.error(500, "Oops. Something went wrong on our side"))
}