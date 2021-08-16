package sentiment.http

import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import io.circe.generic.auto._
import sentiment.model._
import sentiment.http.JsonHelper._


class TwitterHttpClient(http: HttpExt, val baseURL: String, bearerToken: String)(implicit mat: Materializer, ec: ExecutionContext) extends LazyLogging {


  def fetchTweet(tweetUrl: TweetURL): Future[Option[TweetEnhanced]] = {
    val request = HttpRequest(HttpMethods.GET,
                              Uri(baseURL).withQuery(Query("ids" -> tweetUrl.id, "tweet.fields" -> "author_id,created_at")),
                              Seq(Authorization(OAuth2BearerToken(bearerToken))))
    logger.debug(s"request=[$request]")
    http.singleRequest(request) flatMap {
      r =>
        r.status.intValue() match {
          case 200 =>
            Unmarshal(r.entity)
              .to[Tweets]
              .map {
                t =>
                  t.data match {
                    case Some(data) =>
                      logger.info(s"Received success response for id=[${tweetUrl.id}]")
                      data.headOption.map(t => TweetEnhanced(t, tweetUrl.author, tweetUrl.url))
                    case _ =>
                      logger.warn(s"Received error response for id=[${tweetUrl.id}]")
                      None
                  }
              }
              .recover {
                case e: Exception =>
                  logger.error(s"There was an error for id=[${tweetUrl.id}], error=[$e]", e)
                  None
              }
          case undexpected =>
            logger.error(s"There was an error unexpected status=[$undexpected], for id=[${tweetUrl.id}]")
            Future.successful(None)
        }
    } recover {
      case e: Exception =>
        logger.error(s"There was an error for id=[${tweetUrl.id}], error=[$e]", e)
        None
    }

  }

}
