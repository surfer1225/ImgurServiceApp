package services

import java.util.{Date, UUID}

import com.google.inject.ImplementedBy
import javax.inject.Inject
import models.Responses.{ImageUploadStatus, Uploaded}
import utils.ImageFileUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

@ImplementedBy(classOf[ImgurServiceImpl])
trait ImgurService {
  def getImageLinks: Seq[String]

  def getUploadJobStatus(jobId: String): ImageUploadStatus

  //TODO: consider customize type as jobId
  def uploadImage(urls: Seq[String]): String
}

class ImgurServiceImpl @Inject()(imgurJobService: ImgurJobService) extends ImgurService with ImgurLogger {
  override def getImageLinks: Seq[String] = {
    List("abc", "def")
  }

  override def getUploadJobStatus(jobId: String): ImageUploadStatus = {
    ImageUploadStatus(
      UUID.randomUUID().toString,
      new Date(),
      new Date(),
      "in-progress",
      Uploaded(List("a"), List("b", "c"), Nil)
    )
  }

  override def uploadImage(urls: Seq[String]): String = {
    processImages(urls)
    UUID.randomUUID().toString
  }

  /**
    * side effect function to process all uploaded image urls
    * @param urls: image urls
    */
  private def processImages(urls: Seq[String]): Unit = {
    urls foreach { url =>
      ImageFileUtil.decode(url).onComplete {
        case Success(base64Str) =>
          //do upload to Imgur
          imgurJobService.upload(base64Str)
        case Failure(ex) => logger.error(s"Error occurred while resolving decoded image string ${ex.getMessage}")
      }
    }
  }
}
