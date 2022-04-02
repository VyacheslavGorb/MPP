package tasks

import akka.actor.ActorSystem
import models.dao.TaskDao
import play.api.Configuration

import java.io.File
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.util.Try

@Singleton
class RemoveFilesTask @Inject() (config: Configuration, actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) {
  actorSystem.scheduler.scheduleAtFixedRate(initialDelay = 0.seconds, interval = 1.day) { () =>
    val files = findFilesToRemove()
    files.foreach(f => Try(f.delete))
  }

  private def findFilesToRemove(): Seq[File] ={
    val basePath = config.get[String]("base.file.path")
    val uuids = TaskDao.selectFilesUuids()
    new File(basePath).listFiles(file => file.isFile && !uuids.contains(file.getName))
  }
}