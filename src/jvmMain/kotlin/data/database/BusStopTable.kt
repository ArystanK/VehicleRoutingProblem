package data.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object BusStopTable : IntIdTable("bus_stop") {
    val busStopsCollection: Column<Int> = integer("bus_stops_id")
        .references(BusStopsTable.id)
    val latitude: Column<Double> = double("latitude")
    val longitude: Column<Double> = double("longitude")
    val address: Column<String> = varchar("address", 50)
}

class BusStopEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BusStopEntity>(BusStopTable)

    var busStopsCollection by BusStopsEntity referencedOn BusStopTable.busStopsCollection
    var latitude by BusStopTable.latitude
    var longitude by BusStopTable.longitude
    var address by BusStopTable.address
}