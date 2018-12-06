package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import services.ImgurJobService

/**
  * this controller handles all the imgur related requests
  */
@Singleton
class ImgurController @Inject()(cc: ControllerComponents, imgurJobService: ImgurJobService)
    extends AbstractController(cc) {
  def getImageLinks: Action[AnyContent] = Action {
    Ok(Json.prettyPrint(Json.obj("uploaded" -> Json.toJson(imgurJobService.getImageLinks))))
  }

  def getUploadJobStatus(jobId: String): Action[AnyContent] = Action {
    val imageUploadStatus = imgurJobService.getUploadJobStatus(jobId)
    Ok(Json.prettyPrint(Json.toJson(imageUploadStatus)))
  }

  def uploadImageUrls: Action[AnyContent] = Action { request: Request[AnyContent] =>
    val jsBody: Option[JsValue] = request.body.asJson
    jsBody
      .map { body =>
        (body \ "urls").validate[Seq[String]] match {
          case s: JsSuccess[Seq[String]] =>
            Ok(imgurJobService.processUrls(s.get))
          case e: JsError =>
            BadRequest("Error parsing urls as a list of string")
        }
      }
      .getOrElse(BadRequest("Expecting application/json request body with urls in request body"))
  }
}
