package controllers

import java.util.UUID

import models.Responses.{ImageUploadStatus, Uploaded}
import org.specs2.mock.Mockito
import play.api.mvc._
import play.api.test._
import services.ImgurJobService

class ImgurControllerSpec extends PlaySpecification with Results with Mockito {

  "ImgurController#getImageLinks" should {
    "get the right urls" in {
      val imgurJobService = mock[ImgurJobService]
      val controller = new ImgurController(Helpers.stubControllerComponents(), imgurJobService)
      imgurJobService.getImageLinks returns Seq("a","b")
      val result = controller.getImageLinks.apply(FakeRequest())
      (contentAsJson(result) \ "uploaded").as[Seq[String]] mustEqual Seq("a", "b")
    }
  }

  "ImgurController#getUploadJobStatus" should {
    "get the job status correctly" in {
      val imgurJobService = mock[ImgurJobService]
      val controller = new ImgurController(Helpers.stubControllerComponents(), imgurJobService)
      val uuid = UUID.randomUUID().toString
      imgurJobService.getUploadJobStatus(uuid) returns Some(ImageUploadStatus(uuid, "created", "finished", "finished", Uploaded(Nil, Nil, Seq("a"))))
      val result = controller.getUploadJobStatus(uuid).apply(FakeRequest())
      contentAsJson(result).as[ImageUploadStatus] mustEqual ImageUploadStatus(uuid, "created", "finished", "finished", Uploaded(Nil, Nil, Seq("a")))
    }
  }
}
