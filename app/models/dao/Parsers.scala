package models.dao

import anorm.SqlParser.{get, long, str}
import anorm.{RowParser, ~}
import models.entity.{ListTask, ListTaskFile, User}

import java.time.LocalDateTime

object Parsers {
  val userParser: RowParser[User] =
    long("u_id") ~ str("u_login") ~ str("u_password") map {
      case id ~ login ~ password => User(login, password, id)
    }

  val taskParser: RowParser[ListTask] =
    long("t_id") ~
      long("t_user_id") ~
      str("t_title") ~
      str("t_description") ~
      str("t_status") ~
      get[LocalDateTime]("t_start_datetime") ~
      get[LocalDateTime]("t_end_datetime") map {
      case id ~ userId ~ title ~ description ~ status ~ start ~ end =>
        ListTask(userId, title, description, status, start, end, id)
    }

  val taskFileParser: RowParser[ListTaskFile] =
    long("tf_id") ~
      long("tf_task_id") ~
      str("tf_uuid") ~
      str("tf_file_name") map {
      case id ~ taskId ~ uuid ~ fileName => ListTaskFile(taskId, uuid, fileName, id)
    }

}
