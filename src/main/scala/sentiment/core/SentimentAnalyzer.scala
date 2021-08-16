package sentiment.core

import com.typesafe.scalalogging.LazyLogging
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree
import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.util.CoreMap

import java.util.Properties
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

class SentimentAnalyzer(props: Properties) extends LazyLogging {

  def detect(text: String): SentimentType = {

    logger.info("Detecting Sentiment")

    val pipeline = new StanfordCoreNLP(props)
    val annotation: Annotation = pipeline.process(text)
    val sentiments: ListBuffer[Double] = ListBuffer.empty
    val sizes     : ListBuffer[Int] = ListBuffer.empty
    var longest      : Int = 0
    var mainSentiment: Int = 0

    for (sentence: CoreMap <- annotation.get(classOf[SentencesAnnotation]).asScala) {
      val tree     : Tree = sentence.get(classOf[SentimentAnnotatedTree])
      val sentiment: Int = RNNCoreAnnotations.getPredictedClass(tree)
      val partText: String = sentence.toString

      if (partText.length() > longest) {
        mainSentiment = sentiment
        longest = partText.length()
      }

      sentiments += sentiment.toDouble
      sizes += partText.length

      logger.debug("text=[" + text + "] sentiment=[" + sentiment + "]")
      logger.debug("size: " + partText.length)

    }

    val averageSentiment: Double = {
      if(sentiments.nonEmpty) sentiments.sum / sentiments.size
      else -1
    }

    val weightedSentiments: mutable.Seq[Double] = sentiments.lazyZip(sizes).map((sentiment, size) => sentiment * size)
    var weightedSentiment : Double  = weightedSentiments.sum / sizes.sum

    if(sentiments.isEmpty) {
      mainSentiment = -1
      weightedSentiment = -1.0
    }

    logger.debug("text=[" + text + "] mainSentiment=[" + mainSentiment + "]")
    logger.debug("text=[" + text + "] averageSentiment=[" + averageSentiment + "]")
    logger.debug("text=[" + text + "] weightedSentiment=[" + weightedSentiment + "]")

    SentimentType(weightedSentiment)
  }
}
