package models.dao

import anorm.RowParser
import anorm.~
import anorm.SqlParser.{int, str}
import models.entity.User

object Parsers {
  val userParser: RowParser[User] =
    int("u_id") ~ str("u_login") ~ str("u_password") map {
      case id ~ login ~ password => User(login, password, id)
    }
}
