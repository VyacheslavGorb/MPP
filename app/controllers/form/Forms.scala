package controllers.form

import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}

object Forms {

  val loginForm: Form[Login] = Form(
    mapping(
      "login" -> nonEmptyText.verifying(_.matches("[A-Za-z][0-9a-zA-Z]{2,19}")),
      "password" -> nonEmptyText.verifying(_.matches("[0-9a-zA-Z]{8,20}")))
    (Login.apply)(Login.unapply)
  )


  val signUpForm: Form[SignUp] = Form(
    mapping(
      "login" -> nonEmptyText.verifying(_.matches("[A-Za-z][0-9a-zA-Z]{2,19}")),
      "password" -> nonEmptyText.verifying(_.matches("[0-9a-zA-Z]{8,20}")),
      "confirm" -> nonEmptyText.verifying(_.matches("[0-9a-zA-Z]{8,20}")))
    (SignUp.apply)(SignUp.unapply)
  )
}

case class Login(login: String, password: String)

case class SignUp(login: String, password: String, passwordConf: String)
