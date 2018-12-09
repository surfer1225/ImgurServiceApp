package services

import java.text.SimpleDateFormat
import java.util.Date

import models.Entities.ImgurJob
import models.Responses.{ImageUploadStatus, Uploaded}
import org.specs2.mock.Mockito
import play.api.libs.ws.WSClient
import play.api.Configuration
import play.api.mvc._
import play.api.test._

class ImgurJobServiceImplSpec extends PlaySpecification with Results with Mockito {
  "ImgurJobServiceImpl#getImageLinks" should {
    "get the right links" in {
      val ws = mock[WSClient]
      val config = mock[Configuration]
      val service = new ImgurJobServiceImpl(ws, config)
      service.jobMap.put("1", ImgurJob(new Date, None, Map("m" -> "finished", "n" -> "failed")))
      service.getImageLinks mustEqual List("m", "n")
    }
  }

  "ImgurJobServiceImpl#processUrls" should {
    "update map correctly" in {
      val ws = mock[WSClient]
      val config = mock[Configuration]
      val service = new ImgurJobServiceImpl(ws, config)
      val jobId = service.processUrls(Seq("a","b"))
      service.jobMap.get(jobId).finished mustEqual None
      service.jobMap.get(jobId).urlStatusMap mustEqual Map("a" -> "pending", "b" -> "pending")
    }
  }

  "ImgurJobServiceImpl#getUploadJobStatus" should {
    "get the right job status" in {
      val ws = mock[WSClient]
      val config = mock[Configuration]
      val service = new ImgurJobServiceImpl(ws, config)
      val date = new Date
      val dateFormatter = new SimpleDateFormat("YYYY-mm-dd'T'hh:mm:ssZ")
      val dateStr = dateFormatter.format(date)
      service.jobMap.put("99", ImgurJob(date, None, Map("x" -> "pending", "y" -> "failed")))
      service.getUploadJobStatus("2") mustEqual None
      service.getUploadJobStatus("99") mustEqual Some(ImageUploadStatus("99", dateStr, null, "in-progress", Uploaded(Seq("x"), Nil, Seq("y"))))
    }

    "get the right job status with all pending" in {
      val ws = mock[WSClient]
      val config = mock[Configuration]
      val service = new ImgurJobServiceImpl(ws, config)
      val date = new Date
      val dateFormatter = new SimpleDateFormat("YYYY-mm-dd'T'hh:mm:ssZ")
      val dateStr = dateFormatter.format(date)
      service.jobMap.put("98", ImgurJob(date, None, Map("m" -> "pending", "n" -> "pending")))
      service.getUploadJobStatus("2") mustEqual None
      service.getUploadJobStatus("98") mustEqual Some(ImageUploadStatus("98", dateStr, null, "pending", Uploaded(Seq("m", "n"), Nil, Nil)))
    }

    "get the right job status as complete" in {
      val ws = mock[WSClient]
      val config = mock[Configuration]
      val service = new ImgurJobServiceImpl(ws, config)
      val date = new Date
      val dateFormatter = new SimpleDateFormat("YYYY-mm-dd'T'hh:mm:ssZ")
      val dateStr = dateFormatter.format(date)
      service.jobMap.put("100", ImgurJob(date, None, Map("m" -> "failed", "n" -> "complete")))
      service.getUploadJobStatus("2") mustEqual None
      service.getUploadJobStatus("100") mustEqual Some(ImageUploadStatus("100", dateStr, null, "complete", Uploaded(Nil, Seq("n"), Seq("m"))))
    }
  }
}
