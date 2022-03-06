package controllers.action

import controllers.error.ErrorHandler.unauthorizedView
import play.api.mvc.{ActionBuilderImpl, BodyParsers, Request, Result}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UnauthorizedAction @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    if (request.session.get("user_login").isEmpty) {
      block(request)
    } else {
      Future.successful(unauthorizedView(request))
    }
  }
}