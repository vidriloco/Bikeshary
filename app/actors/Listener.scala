package actors
import akka.actor._
import extractors.StationReport
import play.api.Logger

class Listener extends Actor {
  
  def receive = {
    case Listener.Reporter => 
      StationReport.update
  }
  
}

object Listener {
  val Reporter = "StationReporter"
}
