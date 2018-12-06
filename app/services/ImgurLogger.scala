package services

import play.api.Logger

trait ImgurLogger {
  val logger: Logger = Logger(getClass)
}
