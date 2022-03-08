package controllers

import controllers.action.AuthorizedAction
import controllers.error.ErrorHandler.notFoundView
import controllers.form.Forms.{addTaskForm, editTaskForm}
import models.service.TaskService
import play.api.Configuration
import play.api.libs.Files
import play.api.mvc._

import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.util.Try

class TaskController @Inject()(cc: ControllerComponents,
                               authorizedAction: AuthorizedAction,
                               config: Configuration)
                              (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def allTasks(): Action[AnyContent] = authorizedAction { implicit request =>
    val userLogin = request.session("user_login")
    val tasks = TaskService.findAllUserTasks(userLogin)
    Ok(views.html.tasks(tasks))
  }

  def tasksWIthStatus(): Action[AnyContent] = authorizedAction { implicit request =>
    val userLogin = request.session("user_login")
    val status = request.getQueryString("status").get
    val tasks = TaskService.findAllUserTasks(userLogin).filter(_.status == status)
    Ok(views.html.tasks(tasks))
  }

  def addTask(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.add_task())
  }

  def handleAddTask(): Action[MultipartFormData[Files.TemporaryFile]] = authorizedAction(parse.multipartFormData) {
    implicit request =>
      val userLogin = request.session("user_login")
      addTaskForm.bindFromRequest.fold(
        _ => notFoundView,
        p => {
          if (p.startDateTime.isAfter(p.endDateTime) || p.endDateTime.isBefore(LocalDateTime.now())) {
            Redirect(routes.TaskController.addTask).flashing("error" -> "true")
          } else {
            val filesWithUUIDs = TaskService.mapWithUUID(request.body.files.map(_.filename))
            saveFiles(request, filesWithUUIDs)
            TaskService.addNewTask(p, filesWithUUIDs, userLogin)
            Redirect(routes.TaskController.allTasks)
          }
        }
      )
  }

  private def saveFiles(request: Request[MultipartFormData[Files.TemporaryFile]],
                        fileNamesWithUUID: Map[String, String]): Unit = {
    val basePath = config.get[String]("base.file.path")
    request.body.files.map(f => {
      val fileNameUUID = fileNamesWithUUID(f.filename)
      f.ref.copyTo(Paths.get(s"$basePath/$fileNameUUID"), replace = true)
    })
  }

  def sendFile(): Action[AnyContent] = authorizedAction { implicit request =>
    val basePath = config.get[String]("base.file.path")
    val userLogin = request.session("user_login")
    val uuidOpt = request.getQueryString("file")
    val fileNameOpt = TaskService.getUserFileName(userLogin, uuidOpt)
    if (fileNameOpt.isDefined) {
      Ok.sendFile(new File(s"$basePath/${uuidOpt.get}"), fileName = _ => Some(fileNameOpt.get))
    } else notFoundView
  }

  def deleteFile(): Action[AnyContent] = authorizedAction { implicit request =>
    val userLogin = request.session("user_login")
    val uuidOpt = request.getQueryString("file")
    TaskService.deleteFile(userLogin, uuidOpt)
    Redirect(routes.TaskController.allTasks)
  }

  def deleteTask(): Action[AnyContent] = authorizedAction { implicit request =>
    val userLogin = request.session("user_login")
    val taskIdOpt = request.getQueryString("task_id")
    TaskService.deleteTask(taskIdOpt, userLogin)
    Redirect(routes.TaskController.allTasks)
  }

  def editTask(): Action[AnyContent] = authorizedAction { implicit request =>
    val userLogin = request.session("user_login")
    val taskIdOpt = request.getQueryString("task_id")
    if (Try(taskIdOpt.get.toLong).isSuccess) {
      val task = TaskService.findUserTaskById(taskIdOpt.get.toLong, userLogin)
      if (task.isDefined) {
        Ok(views.html.update_task(task.get))
      }else notFoundView
    }else notFoundView
  }

  def handleEditTask(): Action[MultipartFormData[Files.TemporaryFile]] = authorizedAction(parse.multipartFormData) { implicit request =>
    val userLogin = request.session("user_login")
    editTaskForm.bindFromRequest.fold(
      _ => notFoundView,
      p => {
        val isValidTask = TaskService.findUserTaskById(p.id, userLogin).isDefined
        if (isValidTask) {
          val filesWithUUIDs = TaskService.mapWithUUID(request.body.files.map(_.filename))
          saveFiles(request, filesWithUUIDs)
          TaskService.updateTask(p, filesWithUUIDs, userLogin)
          Redirect(routes.TaskController.allTasks)
        } else notFoundView
      }
    )
  }
}
