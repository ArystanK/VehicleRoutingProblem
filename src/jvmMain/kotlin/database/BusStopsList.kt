package database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

// Entity for the bus_stops table
object BusStopsList : IntIdTable("bus_stops") {
    val groupId = integer("group_id")
    val busStopId = reference("bus_stop_id", BusStopTable)
    val sourceColumn = varchar("source", 50)
}

class BusStopsListEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BusStopsListEntity>(BusStopsList)

    var groupId by BusStopsList.groupId
    var busStop by BusStopEntity referencedOn BusStopsList.busStopId
    var source by BusStopsList.sourceColumn
}