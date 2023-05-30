package data.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object BusStopsTable : IntIdTable("bus_stops")

class BusStopsEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BusStopsEntity>(BusStopsTable)

}