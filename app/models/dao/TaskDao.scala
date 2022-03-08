package models.dao

import anorm.SQL
import anorm.SqlParser.scalar
import models.entity.{ListTask, ListTaskFile}
import models.pool.ConnectionPool.{transaction, withConnection}

import java.sql.Connection

object TaskDao {

  private val INSERT_TASK = SQL("INSERT INTO tasks(t_user_id, t_title, t_description, t_status, t_start_datetime, t_end_datetime) VALUES ({user_id}, {title}, {description}, {status}, {start_datetime}, {end_datetime})")
  private val INSERT_FILE = SQL("INSERT INTO task_files(tf_task_id, tf_uuid, tf_file_name) VALUES ({task_id}, {uuid}, {file_name})")
  private val SELECT_TASKS_BY_USER_ID = SQL("SELECT * FROM tasks WHERE t_user_id = {user_id} ORDER BY t_title")
  private val SELECT_USER_TASK_BY_ID = SQL("SELECT * FROM tasks WHERE t_id = {id} AND t_user_id = {user_id}")
  private val SELECT_TASK_FILES_BY_TASK_ID = SQL("SELECT * FROM task_files WHERE tf_task_id = {task_id} ORDER BY tf_file_name")
  private val DELETE_FILE_BY_UUID = SQL("DELETE FROM task_files WHERE tf_uuid = {uuid}")
  private val DELETE_TASK_FILES = SQL("DELETE FROM task_files WHERE tf_task_id = {task_id}")
  private val DELETE_TASK = SQL("DELETE FROM tasks WHERE t_id = {task_id}")
  private val UPDATE_TASK = SQL("UPDATE tasks SET t_title = {title}, t_description = {desc}, t_status = {status}, t_start_datetime = {start}, t_end_datetime = {end} WHERE t_id = {id}")
  private val UPDATE_OVERDUE_TASK = SQL("UPDATE tasks SET t_status = 'OVERDUE' WHERE t_end_datetime <= NOW()")
  private val SELECT_FILE_UUIDS = SQL("SELECT tf_uuid FROM task_files")


  def insertNewTask(task: ListTask, files: Seq[ListTaskFile]): Unit = transaction { implicit c =>
    val taskId = INSERT_TASK.on(
      "user_id" -> task.userId,
      "title" -> task.title,
      "description" -> task.description,
      "status" -> task.status,
      "start_datetime" -> task.start,
      "end_datetime" -> task.end
    ).executeInsert(scalar[Long].single)

    insertFiles(files, taskId)
  }

  def updateTask(task: ListTask, files: Seq[ListTaskFile]): Unit = transaction { implicit c =>
    UPDATE_TASK.on(
      "title" -> task.title,
      "desc" -> task.description,
      "status" -> task.status,
      "start" -> task.start,
      "end" -> task.end,
      "id" -> task.id
    ).executeUpdate()
    insertFiles(files, task.id)
  }

  private def insertFiles(files: Seq[ListTaskFile], taskId: Long)(implicit c: Connection): Unit = {
    files.foreach(file =>
      INSERT_FILE.on(
        "task_id" -> taskId,
        "uuid" -> file.uuid,
        "file_name" -> file.fileName
      ).executeInsert()
    )
  }

  def selectTasksByUserId(userId: Long): Seq[ListTask] = withConnection { implicit c =>
    SELECT_TASKS_BY_USER_ID.on("user_id" -> userId).executeQuery().as(Parsers.taskParser.*)
  }

  def selectTaskFiles(taskId: Long): Seq[ListTaskFile] = withConnection { implicit c =>
    SELECT_TASK_FILES_BY_TASK_ID.on("task_id" -> taskId).executeQuery().as(Parsers.taskFileParser.*)
  }

  def deleteFileByUUID(uuid: String): Unit = withConnection { implicit c =>
    DELETE_FILE_BY_UUID.on("uuid" -> uuid).executeUpdate()
  }

  def deleteTask(taskId: Long): Unit = transaction { implicit c =>
    DELETE_TASK_FILES.on("task_id" -> taskId).executeUpdate()
    DELETE_TASK.on("task_id" -> taskId).executeUpdate()
  }

  def selectUserTasksById(taskId: Long, userId: Long): Option[ListTask] = withConnection { implicit c =>
    SELECT_USER_TASK_BY_ID.on("id" -> taskId, "user_id" -> userId).executeQuery().as(Parsers.taskParser.singleOpt)
  }

  def updateOverdueTasks(): Unit = withConnection { implicit c =>
    UPDATE_OVERDUE_TASK.executeUpdate()
  }

  def selectFilesUuids(): Seq[String] = withConnection { implicit c =>
    SELECT_FILE_UUIDS.executeQuery().as(scalar[String].*)
  }
}
