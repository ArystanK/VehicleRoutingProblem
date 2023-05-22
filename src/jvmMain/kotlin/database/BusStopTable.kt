package database

import org.jetbrains.exposed.dao.id.IntIdTable

object BusStopTable : IntIdTable("bus_stops") {
    val lat = double("lat")
    val lon = double("lon")
    val address = text("address")
}