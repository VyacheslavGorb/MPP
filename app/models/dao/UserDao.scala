package models.dao

import anorm.SqlParser.scalar
import anorm.{SQL, SqlQuery}
import models.entity.User
import models.pool.ConnectionPool.withConnection

object UserDao {

  val INSERT_USER: SqlQuery = SQL("INSERT INTO users(U_login, u_password) VALUES ({login}, {password})")
  val SELECT_USER_BY_ID: SqlQuery = SQL("SELECT * FROM users WHERE u_id = {id}")
  val SELECT_USER_BY_LOGIN: SqlQuery = SQL("SELECT * FROM users WHERE u_login = {login}")

  def insertUser(user: User): User = withConnection { implicit c =>
    val id = INSERT_USER.on("login" -> user.login, "password" -> user.password)
      .executeInsert(scalar[Long].single)
    User(user.login, user.password, id)
  }

  def selectUserByLogin(login: String): Option[User] = withConnection { implicit c =>
    SELECT_USER_BY_LOGIN.on("login" -> login).executeQuery().as(Parsers.userParser.singleOpt)
  }
}
