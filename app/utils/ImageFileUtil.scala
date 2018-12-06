package utils

import java.net.URL
import java.util.Base64

import org.apache.commons.io.IOUtils

object ImageFileUtil {
  def decode(url: String): String = {
    Base64.getEncoder.encodeToString(IOUtils.toByteArray(new URL(url)))
  }
}
