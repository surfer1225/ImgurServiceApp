package models

import java.util.Date

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

object Entities {
  case class Data(id: String, datetime: Long)
  case class ImgurImageUploadResp(data: Data, success: Boolean, status: Int)

  // map to hold the url of all status
  case class ImgurJob(created: Date, finished: Option[Date], urlStatusMap: Map[String, String])

  //implicit reads
  implicit val uploadedReads: Reads[Data] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "datetime").read[Long]
  )(Data.apply _)

  implicit val imgurImageUploadRespReads: Reads[ImgurImageUploadResp] = (
    (JsPath \ "data").read[Data] and
      (JsPath \ "success").read[Boolean] and
      (JsPath \ "status").read[Int]
  )(ImgurImageUploadResp.apply _)
}
