package models

import java.util.Date

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

object Responses {
  //TODO: status to enum
  case class Uploaded(pending: Seq[String], complete: Seq[String], failed: Seq[String])
  case class ImageUploadStatus(id: String, created: Date, finished: Date, status: String, uploaded: Uploaded)

  //implicit reads
  implicit val uploadedReads: Reads[Uploaded] = (
    (JsPath \ "pending").read[Seq[String]] and
      (JsPath \ "complete").read[Seq[String]] and
      (JsPath \ "failed").read[Seq[String]]
  )(Uploaded.apply _)

  implicit val imgurImageUploadRespReads: Reads[ImageUploadStatus] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "created").read[Date] and
      (JsPath \ "finished").read[Date] and
      (JsPath \ "status").read[String] and
      (JsPath \ "uploaded").read[Uploaded]
  )(ImageUploadStatus.apply _)
}
