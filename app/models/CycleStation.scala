package models

import java.net.URLDecoder
import scala.collection.mutable.HashMap
import scala.collection.mutable.Map

import java.util.Date
import java.text.DateFormat
import java.util.Calendar
import java.util.{ArrayList, List}
import scala.collection.JavaConversions._
import java.text.SimpleDateFormat
import com.vividsolutions.jts.geom.Point
import anorm._
import anorm.SqlParser._
import play.api.db._
import anorm.NotAssigned
import play.api.Play.current

case class CycleStation (
  id: Pk[Long],
  name: String,
  number: Int,
  freeSlots: Int,
  bikesAvailable: Int,
  coordinate : Map[String, Double],
  createdAt: Date,
  updatedAt: Date,
  agency: String)

object CycleStation {
  
  val tuple = {
    get[Pk[Long]]("id") ~
    get[String]("name") ~
    get[Int]("number") ~
    get[Int]("free_slots") ~
    get[Int]("bikes_available") ~
    get[String]("coordinates") ~
    get[Date]("created_at") ~
    get[Date]("updated_at") ~
    get[String]("agency") map {
      case id~name~number~freeSlots~bikesAvailable~coordinates~createdAt~updatedAt~agency =>
        CycleStation(id, name, number, freeSlots, bikesAvailable, parseCoordinateString(coordinates), createdAt, updatedAt, agency)
    }
  }
  
  def create(cyclestation: CycleStation): Unit = {
    val timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    DB.withTransaction { implicit connection =>
      SQL("insert into cycle_stations(name, number, free_slots, bikes_available, coordinates, agency, created_at, updated_at) " +
                      "values ({name}, {number}, {free_slots}, {bikes_available}, ST_SetSRID(ST_MakePoint({lon}, {lat}), 4326), {agency}, {created_at}, {updated_at})").on(
        'name -> cyclestation.name,
        'number -> cyclestation.number,
        'free_slots -> cyclestation.freeSlots,
        'bikes_available -> cyclestation.bikesAvailable,
        'lat -> cyclestation.coordinate("lat"),
        'lon -> cyclestation.coordinate("lon"),
        'created_at -> new java.sql.Timestamp(cyclestation.createdAt.getTime()),
        'updated_at -> new java.sql.Timestamp(cyclestation.updatedAt.getTime()),
        'agency -> cyclestation.agency
      ).executeUpdate()
    }
  }
  
  def parseCoordinateString(coordinate : String) : Map[String, Double] = {
    val geom = new com.vividsolutions.jts.io.WKTReader().read(coordinate)

    geom match {
    	case point: Point => return Map("lon" -> point.getX(), "lat" -> point.getY())
        case _ => throw new ClassCastException
    }
  }
  
  def findFirst(number : Int) : Any = {
    DB.withConnection { implicit connection =>
      val cycleStationSeq : List[Any] = SQL("SELECT id FROM cycle_stations WHERE number = {number}").on("number" -> number).list()
      if(cycleStationSeq.size > 0) {
            return cycleStationSeq.head
      } else {
        return null
      } 
    }
  }
  
  def insertNew(cycleStationData : HashMap[String, Any], coordinates: HashMap[String, Double], agency : String) { 
	  val number = cycleStationData("number").asInstanceOf[Int]
	  val free = cycleStationData("freeSlots").asInstanceOf[Int]
	  val available = cycleStationData("bikesAvailable").asInstanceOf[Int]
	  if(this.findFirst(cycleStationData("number").asInstanceOf[Int]) == null) {
	    val cycleStation = CycleStation(NotAssigned,
	    	cycleStationData("name").asInstanceOf[String],
	    	number,
	    	free,
	    	available,
	    	coordinates, Calendar.getInstance.getTime, Calendar.getInstance.getTime, agency)
	    this.create(cycleStation)
	  } else {
	    println(number)
	    DB.withConnection { implicit connection =>
	    SQL("UPDATE cycle_stations SET free_slots = {free}, bikes_available = {bikesAvailable}, updated_at = {updatedAt} WHERE number = {number}").on(
	        "number" -> number, 
	        "free" -> free, 
	        "bikesAvailable" -> available,
	        "updatedAt" -> new java.sql.Timestamp(Calendar.getInstance.getTime.getTime())).executeUpdate()
	    }
	  }
	  
      
  }
}