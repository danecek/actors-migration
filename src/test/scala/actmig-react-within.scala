/**
 * NOTE: Code snippets from this test are included in the Actor Migration Guide. In case you change
 * code in these tests prior to the 2.10.0 release please send the notification to @vjovanov.
 */
package scala.actors.migration
import scala.actors.Actor._
import scala.actors._
import scala.actors.migration._
import java.util.concurrent.{ TimeUnit, CountDownLatch }
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.concurrent.{ Promise, Await }

/**
 * This test shows the migration from Scala TIMEOUT to ReceiveTimeout in Akka.
 */
class ReactWithin extends PartestSuite with ActorSuite {
  val checkFile = "actmig-react-within"
  import org.junit._

  @Test
  def testReactWithinScala() = {
    val finished = Promise[Boolean]
    val sActor = actor {
      loop {
        reactWithin(1) {
          case scala.actors.TIMEOUT =>
            println("received")
            finished success true
            exit()
          case _ =>
            println("Should not occur.")
        }
      }
    }
    Await.ready(finished.future, 20 seconds)
    assertPartest()
  }

  @Test
  def testReactWithinAkka() = {
    val finished = Promise[Boolean]
    val myActor = ActorDSL.actor(new ActWithStash {

      context.setReceiveTimeout(1 millisecond)

      def receive = {
        case ReceiveTimeout =>
          println("received")
          finished success true
          context stop self
        case _ =>
          println("Should not occur.")
      }
    })
    Await.ready(finished.future, 20 seconds)
    assertPartest()
  }

}
