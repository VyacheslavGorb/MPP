package controllers.form


import play.api.data.Form
import play.api.data.Forms.{localDateTime, longNumber, mapping, nonEmptyText}

import java.time.LocalDateTime

object Forms {

  private val DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm"

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

  val addTaskForm: Form[AddTask] = Form(
    mapping(
      "title" -> nonEmptyText.verifying(c => c.length <= 50),
      "desc" -> nonEmptyText.verifying(c => c.length <= 300),
      "start" -> localDateTime(DATE_TIME_PATTERN),
      "end" -> localDateTime(DATE_TIME_PATTERN),
      "status" -> nonEmptyText.verifying(s => Seq("PLANNED", "IN_PROGRESS", "DONE").contains(s)),
    )(AddTask.apply)(AddTask.unapply)
  )

  val editTaskForm: Form[EditTask] = Form(
    mapping(
      "task_id" -> longNumber.verifying(_ >= 0),
      "title" -> nonEmptyText.verifying(c => c.length <= 50),
      "desc" -> nonEmptyText.verifying(c => c.length <= 300),
      "start" -> localDateTime(DATE_TIME_PATTERN),
      "end" -> localDateTime(DATE_TIME_PATTERN),
      "status" -> nonEmptyText.verifying(s => Seq("PLANNED", "IN_PROGRESS", "DONE", "OVERDUE").contains(s)),
    )(EditTask.apply)(EditTask.unapply)
  )
}

case class Login(login: String, password: String)

case class SignUp(login: String, password: String, passwordConf: String)

case class AddTask(title: String, description: String, startDateTime: LocalDateTime, endDateTime: LocalDateTime, status: String)

case class EditTask(id: Long, title: String, description: String, startDateTime: LocalDateTime, endDateTime: LocalDateTime, status: String)