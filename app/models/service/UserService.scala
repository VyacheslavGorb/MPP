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

  def userExists(login: String): Boolean ={
    UserDao.selectUserByLogin(login).isDefined
  }

  def isCorrectPassword(login: String, password: String): Boolean = {
    val user = UserDao.selectUserByLogin(login)
    user.isDefined && PasswordUtil.checkPasswords(password, user.get.password)
  }
}
