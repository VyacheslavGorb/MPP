package utils

import org.springframework.security.crypto.bcrypt.BCrypt

object PasswordUtil {
  private val ROUNDS_COUNT = 12

  def generateHash(password: String): String = {
    val salt = BCrypt.gensalt(ROUNDS_COUNT)
    BCrypt.hashpw(password, salt)
  }

  def checkPasswords(password: String, hashed: String): Boolean = {
    BCrypt.checkpw(password, hashed)
  }
}
