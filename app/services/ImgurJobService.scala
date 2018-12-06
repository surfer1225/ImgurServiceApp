package services

import java.util.concurrent.ConcurrentHashMap
import java.util.{Date, UUID}

import akka.stream.scaladsl.Source
import com.google.inject.ImplementedBy
import javax.inject.Inject
import models.Entities.{ImgurImageUploadResp, ImgurJob}
import models.Responses.{ImageUploadStatus, Uploaded}
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.MultipartFormData.DataPart
import utils.ImageFileUtil

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

@ImplementedBy(classOf[ImgurJobServiceImpl])
trait ImgurJobService {
  def getImageLinks: Seq[String]
  def processUrls(base64Img: Seq[String]): String
  def getUploadJobStatus(jobId: String): ImageUploadStatus
}

class ImgurJobServiceImpl @Inject()(ws: WSClient) extends ImgurJobService with ImgurLogger {

  val jobMap: ConcurrentHashMap[String, ImgurJob] = new ConcurrentHashMap[String, ImgurJob]()

  //TODO: move all this to config
  val imgurApiUrl         = "https://api.imgur.com/3/image"
  val authorizationHeader = "Authorization"
  val authorizationToken  = "Bearer c2ab7e8f75abed853b0a2189f69fce0ac158e5fe"

  override def getImageLinks: Seq[String] = {
    jobMap.values().asScala.flatMap(_.urlStatusMap.values).toList
  }

  override def getUploadJobStatus(jobId: String): ImageUploadStatus =
    imgurJobToUploadStatus(jobId, jobMap.get(jobId))

  private def imgurJobToUploadStatus(jobId: String, imgurJob: ImgurJob): ImageUploadStatus = {
    val statusUrlMap = imgurJob.urlStatusMap.groupBy(_._2).map {
      case (status, statusMap) => (status, statusMap.keys.toList)
    }
    val uploaded = Uploaded(statusUrlMap("pending"), statusUrlMap("complete"), statusUrlMap("failed"))
    val uploadStatus = uploaded match {
      case Uploaded(Nil, _, _)    => "complete"
      case Uploaded(_, Nil, Nil)  => "pending"
      case Uploaded(_ :: _, _, _) => "in-progress"
    }
    ImageUploadStatus(jobId, imgurJob.created, imgurJob.finished.getOrElse(null), uploadStatus, uploaded)
  }

  override def processUrls(urls: Seq[String]): String = {
    val jobId = UUID.randomUUID().toString
    addToMap(jobId, urls)
    urls.map(ImageFileUtil.decode).foreach(_.map(processUrl))
    jobId
  }

  private def addToMap(jobId: String, urls: Seq[String]): Unit = {
    val urlStatusMap = urls.map(_ -> "pending").toMap
    jobMap.put(jobId, ImgurJob(new Date(), None, urlStatusMap))
  }

  // side effect of uploading the image as base64 string
  private def processUrl(base64Img: String): Unit = {
    val wsResponse: Future[WSResponse] = ws
      .url(imgurApiUrl)
      .addHttpHeaders(authorizationHeader -> authorizationToken)
      .post(Source.single(DataPart("image", base64Img)))
    wsResponse.onComplete {
      case Success(response) =>
        logger.info(s"response data: ${response.body}")
        println(s"response data: ${response.body}")
        val resp   = response.json.as[ImgurImageUploadResp]
        val status = resp.status
        println(s"http status: $status")
        logger.info(s"http status: $status")
      case Failure(ex) =>
        logger.error(s"Error resolving Imgur API response: ${ex.getMessage}")
    }
  }
}
