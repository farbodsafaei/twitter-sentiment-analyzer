### Twitter Sentiment Analyzer

This application is a demonstration/proof of concept of sentiment analysis of a series/stream of tweets. 

- Analysis is done by using Stanford Core NLP library and a trained model (Stanford Core NLP Model)
- Application makes use of few Akka Framework's libraries such as Akka streams and Akka HTTP to achieve the result

Application processes data in the following order:
 1. It reads data from a source file in the `root` of the project containing basic tweet info (tweet id, URL, auth, etc.) 
 2. Extracts each tweet id, url and other info from each line and construct a basic tweet object
 3. Using Twitter API, fetches a tweet by id
 4. Runs tweet's text through the analyzer and gets a score 
 5. Constructs a tweet object with sentiment score
 6. Writes data to a csv file in the `root` of the project

**Note**
- Data file contains many deleted tweets, when using those tweet ids if API call return error or failure, those tweets are dropped from stream processing
- NLP model Jar file is in `/lib` directory

Application makes use of Flows in Akka stream, demonstrating possibility of substituting Source or Sink with different sources such as a message bus (Queue, Stream or Database).

All flows are in `Processor.scala` (start method) 

Source of input file:
http://nlp.uned.es/replab2013/replab2013-dataset.tar.gz
via
https://github.com/awesomedata/awesome-public-datasets

#### Important
If any errors occurs from Stanford Core NLP complaining about NLP models missing, please download `stanford-corenlp-4.2.2-models.jar` from https://stanfordnlp.github.io/CoreNLP/download.html and add it to `/lib` directory.  

### How to Run
Run `Main.scala` using sbt:
```shell
sbt run
```

