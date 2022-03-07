package models.service

import models.dao.{TaskDao, UserDao}
import models.entity.{ListTask, ListTaskFile}

import java.time.LocalDateTime
import java.util.UUID

object TaskService {
  private val DEFAULT_STATUS = "PLANNED"

  def addNewTask(title: String, description: String, start: LocalDateTime, end: LocalDateTime,
                 filesWithUUIDs: Map[String, String], userLogin: String): Unit = {
    val user = UserDao.selectUserByLogin(userLogin).get
    val task = ListTask(user.id,title, description, DEFAULT_STATUS, start, end)
    val files = filesWithUUIDs.map{
      case (fileName, uuid) => ListTaskFile(task.id, uuid, fileName)
    }.toList
    TaskDao.insertNewTask(task, files)
  }

  def mapWithUUID(fileNames: Seq[String]): Map[String, String] ={
    fileNames.map(fileName => fileName -> UUID.randomUUID.toString).toMap
  }
}
