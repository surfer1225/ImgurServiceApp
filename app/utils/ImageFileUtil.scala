package utils

import java.io.{ByteArrayInputStream, File}
import java.net.URL
import java.util.Base64

import javax.imageio.ImageIO
import org.apache.commons.io.IOUtils

import scala.concurrent.Future

object ImageFileUtil {
  //TODO: add addtional check
  def decode(url: String): Future[String] = {
    val byteArray = IOUtils.toByteArray(new URL(url))
    Future.successful(Base64.getEncoder.encodeToString(byteArray))
  }

  //FIXME: remove this, this is to test the image
  private def saveImage(base64String: String): Unit = {
    val bis   = new ByteArrayInputStream(Base64.getDecoder.decode(base64String))
    val image = ImageIO.read(bis)
    bis.close()

    val outputFile = new File("./image.jpg")
    ImageIO.write(image, "jpg", outputFile)
  }
}
