package models.dao

import anorm.RowParser
import anorm.~
import anorm.SqlParser.{int, str}
import models.entity.User

object Parsers {
  val userParser: RowParser[User] =
    int("id") ~ str("login") ~ str("password") map {
      case id ~ login ~ password => User(id, login, password)
    }
}
