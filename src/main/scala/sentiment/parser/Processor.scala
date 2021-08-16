package sentiment.parser

import akka.NotUsed
import akka.stream.alpakka.csv.scaladsl.CsvFormatting
import akka.stream.{ActorAttributes, IOResult, Materializer, Supervision}
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.util.ByteString
import sentiment.core.SentimentAnalyzer
import sentiment.http.TwitterHttpClient
import sentiment.model._

import java.nio.file.Paths
import java.nio.file.StandardOpenOption.{CREATE, TRUNCATE_EXISTING, WRITE}
import scala.concurrent.Future
import scala.concurrent.duration._

class Processor(inputFile : String,
                outputFile: String,
                httpClient: TwitterHttpClient,
                sentimentAnalyzer: SentimentAnalyzer)(implicit mat: Materializer) {

  def start: Future[IOResult] = source
    .via(extractData)
    .via(dropNonEnglish)
    .via(createTweetObject)
    .via(fetchTweet)
    .via(detectSentiment)
    .via(prepareCsvLine)
    .via(CsvFormatting.format())
    .runWith(sink)

  private def source: Source[String, Future[IOResult]] = FileIO.fromPath(Paths.get(inputFile), 1 * 1024 * 1024)
                                                               .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 1024, allowTruncation = true))
                                                               .map(_.utf8String)

  private def sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(Paths.get(outputFile), options = Set(WRITE, TRUNCATE_EXISTING, CREATE))

  private def extractData: Flow[String, Array[String], NotUsed] = Flow[String]
    .map(str => str.split("\t"))
    .filterNot(array => array(0) == "tweet_id")
    .map(a => a.map(x => x.replaceAll("\"", "")))
    .withAttributes(ActorAttributes.supervisionStrategy(_ => Supervision.Resume))

  private def dropNonEnglish: Flow[Array[String], Array[String], NotUsed] = Flow[Array[String]]
    .filter(array => array(4) == "EN")
    .withAttributes(ActorAttributes.supervisionStrategy(_ => Supervision.Resume))

  private def createTweetObject: Flow[Array[String], TweetURL, NotUsed] = Flow[Array[String]]
    .map(array => TweetURL(array(0), array(1), array(3), 123L))
    .withAttributes(ActorAttributes.supervisionStrategy(_ => Supervision.Resume))

  private def fetchTweet: Flow[TweetURL, TweetEnhanced, NotUsed] = Flow[TweetURL].throttle(900, 15.minutes)
                                                                                 .mapAsync(1)(httpClient.fetchTweet)
                                                                                 .filter(_.isDefined)
                                                                                 .collect({ case Some(t) => t })

  private def detectSentiment: Flow[TweetEnhanced, TweetDetails, NotUsed] = Flow[TweetEnhanced].map {
    t => {
      val sentimentType = sentimentAnalyzer.detect(t.text)
      TweetDetails(t.id, t.author_id, t.author, t.tweet_url, t.text, t.created_at, sentimentType.sentimentScore, sentimentType.description)
    }
  }.withAttributes(ActorAttributes.supervisionStrategy(_ => Supervision.Resume))

  private def prepareCsvLine: Flow[TweetDetails, Seq[String], NotUsed] = Flow[TweetDetails].map {
    t =>
      Seq(t.id, t.authorId, t.author,  t.url, t.timestamp.toString, t.text, t.sentimentScore.toString, t.sentimentDescription)
  }.withAttributes(ActorAttributes.supervisionStrategy(_ => Supervision.Resume))


}
