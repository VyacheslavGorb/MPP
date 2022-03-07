package models.dao

import anorm.SQL
import anorm.SqlParser.scalar
import models.entity.{ListTask, ListTaskFile}
import models.pool.ConnectionPool.transaction

object TaskDao {

  private val INSERT_TASK = SQL("INSERT INTO tasks(t_user_id, t_title, t_description, t_status, t_start_datetime, t_end_datetime) VALUES ({user_id}, {title}, {description}, {status}, {start_datetime}, {end_datetime})")
  private val INSERT_FILE = SQL("INSERT INTO task_files(tf_task_id, tf_uuid, tf_file_name) VALUES ({task_id}, {uuid}, {file_name})")

  def insertNewTask(task: ListTask, files: Seq[ListTaskFile]): Unit = transaction { implicit c =>
    val taskId = INSERT_TASK.on(
      "user_id" -> task.userId,
      "title" -> task.title,
      "description" -> task.description,
      "status" -> task.status,
      "start_datetime" -> task.start,
      "end_datetime" -> task.end
    ).executeInsert(scalar[Long].single)

    files.foreach(file =>
      INSERT_FILE.on(
        "task_id" -> taskId,
        "uuid" -> file.uuid,
        "file_name" -> file.fileName
      ).executeInsert()
    )
  }
}
