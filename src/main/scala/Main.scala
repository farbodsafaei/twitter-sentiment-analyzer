import akka.Done
import sentiment.Services

import scala.util.{Failure, Success}

object Main extends App with Services {

 processor.start.onComplete {
  case Success(_) =>
   println(Done)
   System.exit(0)
  case Failure(e) =>
   e.printStackTrace()
   System.exit(-1)
 }

}
