package controllers

import controllers.action.AuthorizedAction
import controllers.error.ErrorHandler.notFoundView
import controllers.form.Forms.addTaskForm
import models.service.TaskService
import play.api.Configuration
import play.api.libs.Files
import play.api.mvc._

import java.io.File
import java.nio.file.Paths
import javax.inject.Inject
import scala.concurrent.ExecutionContext

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

  def addTask(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.add_task())
  }

  def handleAddTask(): Action[MultipartFormData[Files.TemporaryFile]] = authorizedAction(parse.multipartFormData) {
    implicit request =>
      val userLogin = request.session("user_login")
      addTaskForm.bindFromRequest.fold(
        _ => notFoundView,
        p => {
          if (p.startDateTime.isAfter(p.endDateTime)) {
            Redirect(routes.TaskController.addTask).flashing("error" -> "true")
          } else {
            val filesWithUUIDs = TaskService.mapWithUUID(request.body.files.map(_.filename))
            saveFiles(request, filesWithUUIDs)
            TaskService.addNewTask(p.title, p.description, p.startDateTime, p.endDateTime, filesWithUUIDs, userLogin)
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
    if(fileNameOpt.isDefined){
      Ok.sendFile(new File(s"$basePath/${uuidOpt.get}"), fileName = _ => Some(fileNameOpt.get))
    }else notFoundView
  }

  def deleteFile(): Action[AnyContent] = authorizedAction{implicit request =>
    val userLogin = request.session("user_login")
    val uuidOpt = request.getQueryString("file")
    TaskService.deleteFile(userLogin, uuidOpt)
    Redirect(routes.TaskController.allTasks)
  }
}
