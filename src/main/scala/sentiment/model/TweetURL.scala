package sentiment.model

import java.time.ZonedDateTime

case class TweetURL(id: String, author: String, url: String)

case class TweetEnhanced(id: String, text: String, author_id: String, author: String, tweet_url: String, created_at: ZonedDateTime)

object TweetEnhanced {
  def apply(t: Tweet, author: String, url: String): TweetEnhanced = TweetEnhanced(t.id, t.text, t.author_id, author, url, t.created_at)
}