package sentiment

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

package object model {

  // ISO 8601
  private lazy val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE

  // json decoders
  lazy val zonedDateTime: Decoder[ZonedDateTime] = Decoder[String].map(ZonedDateTime.parse(_, dateTimeFormatter))
  lazy val tweetDecoder: Decoder[Tweet] = deriveDecoder[Tweet]
}
