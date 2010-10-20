package org.scalatra

import java.security.{ MessageDigest, SecureRandom }
import java.util.Locale

trait CSRFTokenSupport { self: ScalatraKernel =>

  private val WRITE_METHODS = "POST" :: "PUT" :: "DELETE" :: Nil
  private val CSRF_KEY = "csrfToken"

  before {
    if (WRITE_METHODS.contains(request.getMethod.toUpperCase(Locale.ENGLISH)) &&
            session.get(CSRF_KEY) != params.get(CSRF_KEY))
      halt(403, "Request tampering detected!")
    prepareCSRFToken
  }

  protected def prepareCSRFToken = {
    val token = generateCSRFToken
    session(CSRF_KEY) = token
  }

  private def hexEncode(bytes: Array[Byte]) =  ((new StringBuilder(bytes.length * 2) /: bytes) { (sb, b) =>
    if((b.toInt & 0xff) < 0x10) sb.append("0")
    sb.append(Integer.toString(b.toInt & 0xff, 16))
  }).toString

  protected def generateCSRFToken = {
    val digest = MessageDigest.getInstance("MD5")
    val tokenVal = new Array[Byte](20)
    (new SecureRandom).nextBytes(tokenVal)
    hexEncode(digest.digest(tokenVal))
  }
}