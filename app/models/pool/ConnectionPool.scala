package models.pool

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.slf4j.LoggerFactory

import java.sql.Connection
import scala.util.{Failure, Success, Try}

object ConnectionPool {
  private val logger = LoggerFactory.getLogger(ConnectionPool.getClass)
  private val PROPERTIES_PATH = "hikari.properties"
  private val dataSource = new HikariDataSource(new HikariConfig(
    getClass.getClassLoader.getResource(PROPERTIES_PATH).getPath
  ))

  def getConnection: Connection = dataSource.getConnection

  def withConnection[T](func: Connection => T): T = {
    val c = getConnection
    val result = Try(func.apply(c)) match {
      case Success(value) => value
      case Failure(e) =>
        logger.error("Error while executing query", e)
        throw e
    }
    c.close()
    result
  }
}
