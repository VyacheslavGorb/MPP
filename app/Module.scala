import play.api.inject
import play.api.inject.SimpleModule
import tasks.{ChangeStatusTask, RemoveFilesTask}

class Module extends SimpleModule(
  inject.bind[ChangeStatusTask].toSelf.eagerly(),
  inject.bind[RemoveFilesTask].toSelf.eagerly(),
)