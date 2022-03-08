package models.service

import controllers.form.{AddTask, EditTask}
import models.dao.{TaskDao, UserDao}
import models.entity.{ListTask, ListTaskDto, ListTaskFile}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.util.{Failure, Success, Try}

object TaskService {
  private val OVERDUE_STATUS = "OVERDUE"
  private val formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")

  def addNewTask(taskFormData: AddTask, filesWithUUIDs: Map[String, String], userLogin: String): Unit = {
    val user = UserDao.selectUserByLogin(userLogin).get
    import taskFormData._
    val task = ListTask(user.id, title, description, status, startDateTime, endDateTime)
    val files = filesWithUUIDs.map {
      case (fileName, uuid) => ListTaskFile(task.id, uuid, fileName)
    }.toList
    TaskDao.insertNewTask(task, files)
  }

  def updateTask(taskFormData: EditTask, filesWithUUIDs: Map[String, String], userLogin: String): Unit = {
    val user = UserDao.selectUserByLogin(userLogin).get
    import taskFormData._
    val task = ListTask(user.id, title, description, status, startDateTime, endDateTime, id)
    val files = filesWithUUIDs.map {
      case (fileName, uuid) => ListTaskFile(task.id, uuid, fileName)
    }.toList
    TaskDao.updateTask(task, files)
  }

  def mapWithUUID(fileNames: Seq[String]): Map[String, String] = {
    fileNames.map(fileName => fileName -> UUID.randomUUID.toString).toMap
  }

  def findAllUserTasks(login: String): Seq[ListTaskDto] = {
    val user = UserDao.selectUserByLogin(login).get
    val tasks = TaskDao.selectTasksByUserId(user.id)
    createTaskDtos(tasks)
  }

  private def createTaskDtos(tasks: Seq[ListTask]): Seq[ListTaskDto] = {
    tasks.map(task => {
      val files = TaskDao.selectTaskFiles(task.id)
      var status = task.status
      if (task.end.isBefore(LocalDateTime.now())) {
        status = OVERDUE_STATUS
      }
      val formattedStart = formatter.format(task.start)
      val formattedEnd = formatter.format(task.end)
      status = status.split("_").mkString(" ").toLowerCase.capitalize
      ListTaskDto(task.userId, task.title, task.description, status, formattedStart, formattedEnd, files, task.id)
    })
  }

  def findAllUserFiles(login: String): Seq[ListTaskFile] = {
    val user = UserDao.selectUserByLogin(login).get
    val tasks = TaskDao.selectTasksByUserId(user.id)
    tasks.flatMap(task =>
      TaskDao.selectTaskFiles(task.id)
    )
  }

  def getUserFileName(login: String, fileOpt: Option[String]): Option[String] = {
    if (fileOpt.isEmpty) return None
    val userFiles = findAllUserFiles(login)
    val isValidFile = userFiles.map(_.uuid).contains(fileOpt.get)
    if (!isValidFile) return None
    val fileName = userFiles.filter(_.uuid == fileOpt.get).head.fileName
    Some(fileName)
  }

  def deleteFile(login: String, fileOpt: Option[String]): Unit = {
    if (fileOpt.isEmpty) return
    val userFiles = findAllUserFiles(login)
    val isValidFile = userFiles.map(_.uuid).contains(fileOpt.get)
    if (!isValidFile) return
    TaskDao.deleteFileByUUID(fileOpt.get)
  }

  def deleteTask(taskIdOpt: Option[String], login: String): Unit ={
    val taskId =Try(taskIdOpt.get.toLong) match {
      case Failure(_) => return
      case Success(value) => value
    }
    val user = UserDao.selectUserByLogin(login)
    val tasks = TaskDao.selectTasksByUserId(user.get.id)
    if(tasks.map(_.id).contains(taskId)){
      TaskDao.deleteTask(taskId)
    }
  }

  def findUserTaskById(taskId: Long, login: String): Option[ListTask] ={
    val user = UserDao.selectUserByLogin(login)
    TaskDao.selectUserTasksById(taskId, user.get.id)
  }
}
