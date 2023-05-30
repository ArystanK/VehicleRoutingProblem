package data.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object RouteListTable : IntIdTable("routes") {
    val type: Column<String> = varchar("type", 3)
    val busStopsId: Column<Int> = integer("bus_stops_id")
        .references(BusStopsTable.id)
}

class RouteListEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<RouteListEntity>(RouteListTable)

    var type by RouteListTable.type
    var busStops by BusStopsEntity referencedOn RouteListTable.busStopsId
}