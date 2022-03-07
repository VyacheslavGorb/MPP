package models.entity

import java.time.LocalDateTime

case class User(login: String,
                password: String,
                id: Long = -1)

case class ListTask(userId: Long,
                    title: String,
                    description: String,
                    status: String,
                    start: LocalDateTime,
                    end: LocalDateTime,
                    id: Long = -1)

case class ListTaskFile(taskId: Long,
                        uuid: String,
                        fileName: String,
                        id: Long = -1)

case class ListTaskDto(userId: Long,
                       title: String,
                       description: String,
                       status: String,
                       start: LocalDateTime,
                       end: LocalDateTime,
                       files: Seq[ListTaskFile],
                       id: Long = -1)