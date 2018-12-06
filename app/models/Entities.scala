package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

object Entities {
  case class Data(id: String, datetime: Long)
  case class ImgurImageUploadResp(data: Data, success: Boolean, status: Int)

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
