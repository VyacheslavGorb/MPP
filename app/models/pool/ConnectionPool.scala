package models.pool

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import java.sql.Connection

object ConnectionPool {
  private val PROPERTIES_PATH = "hikari.properties"
  private val dataSource = new HikariDataSource(new HikariConfig(
    getClass.getClassLoader.getResource(PROPERTIES_PATH).getPath
  ))

  def getConnection: Connection = dataSource.getConnection

  def withConnection[T](func: Connection => T): T = {
    val connection = getConnection
    try {
      func.apply(connection)
    } finally {
      connection.close()
    }
  }

  def transaction[T](func: Connection => T): T = {
    val connection = getConnection
    try {
      connection.setAutoCommit(false)
      val result = func.apply(connection)
      connection.commit()
      connection.setAutoCommit(true)
      result
    } finally {
      connection.close()
    }
  }
}
