package sentiment

import com.typesafe.config.{Config, ConfigFactory}

trait AppConfig {

  val config: Config = ConfigFactory.load(getClass.getClassLoader)

  val twitterApiKey      = config.getString("twitter.api-key")
  val twitterApiSecret   = config.getString("twitter.api-secret")
  val twitterBearerToken = config.getString("twitter.bearer-token")
  val twitterBaseUrl     = config.getString("twitter.base-url")
  val nlpAnnotators      = config.getString("nlp.annotators")
  val inputFilename      = config.getString("input-filename")
  val outputFilename     = config.getString("output-filename")
}
