package models.service

import models.dao.UserDao
import models.entity.User
import utils.PasswordUtil

object UserService {
  def signUpUser(login: String, password: String): User = {
    val hashed = PasswordUtil.generateHash(password)
    val user = User(login = login, password = hashed)
    UserDao.insertUser(user)
  }

  def userExists(login: String): Boolean = {
    UserDao.selectUserByLogin(login).isDefined
  }

  def findValidUser(login: String, password: String): Option[User] = {
    val user = UserDao.selectUserByLogin(login)
    if (user.isEmpty || !PasswordUtil.checkPasswords(password, user.get.password)) {
      return None
    }
    user
  }
}
