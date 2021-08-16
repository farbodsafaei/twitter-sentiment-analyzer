package sentiment

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import sentiment.core.SentimentAnalyzer
import sentiment.http.TwitterHttpClient
import sentiment.parser.Processor

import java.util.Properties
import scala.concurrent.ExecutionContextExecutor

/**
 *
 */
trait Services extends AppConfig {

  // Akka actor related stuff
  implicit val system      : ActorSystem              = ActorSystem("main-actor")
  implicit val materializer: Materializer             = Materializer(system)
  implicit val executor    : ExecutionContextExecutor = system.dispatcher

  // NLP properties
  lazy val nlpProps: Properties = {
    val props = new Properties()
    props.setProperty("annotators", nlpAnnotators)
    props
  }

  // Services
  lazy val sentimentAnalyzer: SentimentAnalyzer = new SentimentAnalyzer(nlpProps)
  lazy val twitterHttpClient: TwitterHttpClient = new TwitterHttpClient(Http(), twitterBaseUrl, twitterBearerToken)
  lazy val processor        : Processor         = new Processor(inputFilename, outputFilename, twitterHttpClient, sentimentAnalyzer)

}
