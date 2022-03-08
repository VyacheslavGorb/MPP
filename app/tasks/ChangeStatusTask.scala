package tasks

import akka.actor.ActorSystem
import models.dao.TaskDao

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class ChangeStatusTask @Inject() (actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) {
  actorSystem.scheduler.scheduleAtFixedRate(initialDelay = 0.seconds, interval = 1.day) { () =>
    TaskDao.updateOverdueTasks()
  }
}
