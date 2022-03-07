package controllers

import controllers.action.AuthorizedAction
import controllers.error.ErrorHandler.notFoundView
import controllers.form.Forms.addTaskForm
import models.service.TaskService
import play.api.Configuration
import play.api.libs.Files
import play.api.mvc._

import java.nio.file.Paths
import javax.inject.Inject

class TaskController @Inject()(cc: ControllerComponents,
                               authorizedAction: AuthorizedAction,
                               config: Configuration)
  extends AbstractController(cc) {

  def addTask(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.add_task())
  }

  def handleAddTask(): Action[MultipartFormData[Files.TemporaryFile]] = authorizedAction(parse.multipartFormData) {
    implicit request =>
      var userLogin = request.session("user_login")
      addTaskForm.bindFromRequest.fold(
        _ => notFoundView,
        p => {
          val filesWithUUIDs = TaskService.mapWithUUID(request.body.files.map(_.filename))
          saveFiles(request, filesWithUUIDs)
          TaskService.addNewTask(p.title, p.description, p.startDateTime, p.endDateTime, filesWithUUIDs, userLogin)
          Redirect(routes.TaskController.addTask) //TODO redirect to tasks
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
}
