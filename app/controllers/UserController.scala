package controllers

import controllers.action.{AuthorizedAction, UnauthorizedAction}
import controllers.error.ErrorHandler.notFoundView
import controllers.form.Forms.{loginForm, signUpForm}
import models.service.UserService
import play.api.mvc._

import javax.inject.{Inject, Singleton}


@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               unauthorizedAction: UnauthorizedAction,
                               authorizedAction: AuthorizedAction)
  extends AbstractController(cc) {

  def login(): Action[AnyContent] = unauthorizedAction { implicit request =>
    Ok(views.html.login())
  }

  def signUp(): Action[AnyContent] = unauthorizedAction { implicit request =>
    Ok(views.html.signup())
  }

  def handleLogin(): Action[AnyContent] = unauthorizedAction { implicit request =>
    loginForm.bindFromRequest.fold(
      _ => notFoundView,
      params => {
        val user = UserService.findValidUser(params.login, params.password)
        if (user.isDefined)
          Redirect(routes.MainController.index).withSession(request.session + ("user_login" -> params.login))
        else
          Redirect(routes.UserController.login).flashing("error" -> "true")
      }
    )
  }

  def handleSignUp(): Action[AnyContent] = unauthorizedAction { implicit request =>
    signUpForm.bindFromRequest.fold(
      _ => notFoundView,
      params => {
        if (params.password != params.passwordConf) {
          Redirect(routes.UserController.signUp).flashing("error" -> s"Passwords mismatch")
        } else if (UserService.userExists(params.login)) {
          Redirect(routes.UserController.signUp).flashing("error" -> s"User with login ${params.login} already exists")
        } else {
          UserService.signUpUser(params.login, params.password)
          Redirect(routes.MainController.index).withSession(request.session + ("user_login" -> params.login))
        }
      }
    )
  }

  def logout(): Action[AnyContent] = authorizedAction { implicit request =>
    Redirect(routes.MainController.index).withNewSession
  }
}
