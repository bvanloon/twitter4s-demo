import com.danielasfregola.twitter4s.TwitterClient
import com.danielasfregola.twitter4s.entities.{HashTag, Tweet}

import scala.concurrent.ExecutionContext.Implicits.global

object MyTopHashtags extends App {

  def getTopHashtags(tweets: Seq[Tweet], n: Int = 10): Seq[(String, Int)] = {
    val hashtags: Seq[Seq[HashTag]] = tweets.map { tweet =>
      tweet.entities.map(_.hashtags).getOrElse(Seq.empty)
    }
    val hashtagTexts: Seq[String] = hashtags.flatten.map(_.text.toLowerCase)
    val hashtagFrequencies: Map[String, Int] = hashtagTexts.groupBy(identity).mapValues(_.size)
    hashtagFrequencies.toSeq.sortBy { case (entity, frequency) => -frequency }.take(n)
  }

  // TODO - Make sure to change the application.conf file with the right consumer and access tokens!
  val client = new TwitterClient()

  client.getHomeTimeline(count = 200).map { tweets =>
    val topHashtags: Seq[((String, Int), Int)] = getTopHashtags(tweets).zipWithIndex
    val rankings = topHashtags.map { case ((entity, frequency), idx) => s"[${idx + 1}] $entity (found $frequency times)"}
    println("MY TOP HASHTAGS:")
    println(rankings.mkString("\n"))
  }

}