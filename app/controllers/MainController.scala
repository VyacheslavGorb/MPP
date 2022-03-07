package controllers

import play.api.mvc._

import javax.inject._

@Singleton
class MainController @Inject()(cc: ControllerComponents)
  extends AbstractController(cc) {
  def index: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("user_login").isDefined) {
      Redirect(routes.TaskController.allTasks)
    } else {
      Redirect(routes.UserController.login)
    }
  }
}
