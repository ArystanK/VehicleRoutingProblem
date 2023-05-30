package data.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

internal object BusStopTable : IntIdTable("bus_stop") {
    val busStops: Column<EntityID<Int>> =
        reference("bus_stops_id", BusStopsTable.id)
    val latitude: Column<Double> = double("latitude")
    val longitude: Column<Double> = double("longitude")
    val address: Column<String> = text("address")
}

class BusStopEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BusStopEntity>(BusStopTable)

    var busStops by BusStopsEntity referencedOn BusStopTable.busStops
    var latitude by BusStopTable.latitude
    var longitude by BusStopTable.longitude
    var address by BusStopTable.address
}