import play.api._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor.Props
import java.util.concurrent.TimeUnit
import play.api.Logger
import actors.Listener
import scala.concurrent.duration.DurationInt
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object Global extends GlobalSettings {
	
  override def onStart(app: Application) {
    val actorRef = Akka.system.actorOf(Props[Listener])
    
    Akka.system(app).scheduler.schedule(0 seconds, 7 minutes, actorRef, "StationReporter")
  }
  
}
