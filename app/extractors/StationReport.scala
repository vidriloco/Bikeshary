package extractors
import java.net.{DatagramPacket, InetAddress, MulticastSocket, SocketTimeoutException}
import com.codahale.jerkson.Json._
import models.CycleStation
import scala.collection.mutable.HashMap

object StationReport {
	val providerURL : String = "http://api.citybik.es/ecobici.json"

	def update {
	  
	  val urlContent = io.Source.fromURL(providerURL).mkString
	  val json = parse[List[Map[String,String]]](urlContent)
	  
	  for(station <- json) {
	    val stationName = station("name").split(" ").tail.mkString(" ")
	    val stationNumber = station("number").toInt
	    val stationFreeSlots = station("free").toInt
	    val stationBikesAvailable = station("bikes").toInt
	    val stationLatitude = station("lat").toInt/1E6
	    val stationLongitude = station("lng").toInt/1E6
	    val stationProvider = "Ecobici"
	    
        var coordinates = HashMap[String, Double]()
        coordinates += ("lat" -> stationLatitude, "lon" -> stationLongitude)
        
        var params = HashMap[String, Any]()
        params += (
            "name" -> stationName, 
            "number" -> stationNumber, 
            "freeSlots" -> stationFreeSlots, 
            "bikesAvailable" -> stationBikesAvailable)
        
        CycleStation.insertNew(params, coordinates, "Ecobici")
	  }
	  
	}
}
