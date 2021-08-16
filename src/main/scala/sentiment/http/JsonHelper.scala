package sentiment.http

import akka.http.scaladsl.model.{ContentTypeRange, ContentTypes}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import io.circe.Decoder
import io.circe.parser.decode

import scala.concurrent.Future

object JsonHelper {

  implicit final def unmarshaller[A: Decoder]: FromEntityUnmarshaller[A] = {
    Unmarshaller.stringUnmarshaller
                .forContentTypes(List[ContentTypeRange](ContentTypes.`application/json`): _*)
                .flatMap { _ => _ => json =>
                  decode[A](json).fold(Future.failed, Future.successful)
                }
  }

}
