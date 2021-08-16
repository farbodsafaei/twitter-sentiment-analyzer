package sentiment.model

import java.time.ZonedDateTime


case class Tweet(id: String, text: String, author_id: String, created_at: ZonedDateTime)

case class Error(value         : Option[String],
                  detail       : Option[String],
                  title        : Option[String],
                  resource_type: Option[String],
                  parameter    : Option[String],
                  resource_id  : Option[String],
                  `type`       : Option[String])

case class Tweets(data: Option[Seq[Tweet]] = None, errors: Option[Seq[Error]] = None)
