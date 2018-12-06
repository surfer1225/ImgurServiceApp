package models

import java.util.Date

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}

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

  //implicit writes
  implicit val uploadedWrites: Writes[Uploaded] = (
    (JsPath \ "pending").write[Seq[String]] and
      (JsPath \ "complete").write[Seq[String]] and
      (JsPath \ "failed").write[Seq[String]]
  )(unlift(Uploaded.unapply))

  implicit val imgurImageUploadRespWrites: Writes[ImageUploadStatus] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "created").write[Date] and
      (JsPath \ "finished").write[Date] and
      (JsPath \ "status").write[String] and
      (JsPath \ "uploaded").write[Uploaded]
  )(unlift(ImageUploadStatus.unapply))
}
