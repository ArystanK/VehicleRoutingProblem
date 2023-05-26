package database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

// Entity for the bus_stop table
object BusStopTable : IntIdTable("bus_stop") {
    val latitude = double("latitude")
    val longitude = double("longitude")
    val address = varchar("address", 255)
}

class BusStopEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BusStopEntity>(BusStopTable)

    var latitude by BusStopTable.latitude
    var longitude by BusStopTable.longitude
    var address by BusStopTable.address
}