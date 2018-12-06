package services

import akka.stream.scaladsl.Source
import com.google.inject.ImplementedBy
import javax.inject.Inject
import models.Entities.ImgurImageUploadResp
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.MultipartFormData.DataPart

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

@ImplementedBy(classOf[ImgurJobServiceImpl])
trait ImgurJobService {
  def upload(base64Img: String): Unit
}

class ImgurJobServiceImpl @Inject()(ws: WSClient) extends ImgurJobService with ImgurLogger {

  //TODO: move all this to config
  val imgurApiUrl         = "https://api.imgur.com/3/image"
  val authorizationHeader = "Authorization"
  val authorizationToken  = "Bearer c2ab7e8f75abed853b0a2189f69fce0ac158e5fe"

  override def upload(base64Img: String): Unit = {
    val wsResponse: Future[WSResponse] = ws
      .url(imgurApiUrl)
      .addHttpHeaders(authorizationHeader -> authorizationToken)
      .post(Source.single(DataPart("image", base64Img)))
    wsResponse.onComplete {
      case Success(response) =>
        val resp   = response.json.as[ImgurImageUploadResp]
        val status = resp.status
        logger.info(s"http status: $status")
      case Failure(ex) =>
        logger.error(s"Error resolving Imgur API response: ${ex.getMessage}")
    }
  }
}
