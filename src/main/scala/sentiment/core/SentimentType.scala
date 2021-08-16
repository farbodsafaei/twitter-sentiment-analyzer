package sentiment.core

case class SentimentType(sentimentScore: Double) {

  val description: String = sentimentScore match {
    case s if s <= 0.0 => "NotUnderstood"
    case s if s < 1.0 => "VeryNegative"
    case s if s < 2.0 => "Negative"
    case s if s < 3.0 => "Neutral"
    case s if s < 4.0 => "Positive"
    case s if s < 5.0 => "VeryPositive"
    case s if s > 5.0 => "NotUnderstood"
  }

  override def toString: String = description
}
