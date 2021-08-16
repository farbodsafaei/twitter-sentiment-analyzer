package sentiment.model

import java.time.ZonedDateTime

case class TweetDetails(id: String,
                        authorId: String,
                        author: String,
                        url: String,
                        text: String,
                        timestamp: ZonedDateTime,
                        sentimentScore: Double,
                        sentimentDescription: String)
