package models.dao

import anorm.SQL
import anorm.SqlParser.scalar
import models.entity.User
import models.pool.ConnectionPool.withConnection

object UserDao {

  private val INSERT_USER = SQL("INSERT INTO users(U_login, u_password) VALUES ({login}, {password})")
  private val SELECT_USER_BY_LOGIN = SQL("SELECT * FROM users WHERE u_login = {login}")

  def insertUser(user: User): User = withConnection { implicit c =>
    val id = INSERT_USER.on("login" -> user.login, "password" -> user.password)
      .executeInsert(scalar[Long].single)
    User(user.login, user.password, id)
  }

  def selectUserByLogin(login: String): Option[User] = withConnection { implicit c =>
    SELECT_USER_BY_LOGIN.on("login" -> login).executeQuery().as(Parsers.userParser.singleOpt)
  }
}
