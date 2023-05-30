package data.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object RouteTable : IntIdTable("route") {
    val routesId: Column<Int> = integer("routes_id")
        .references(RouteListTable.id)
    val busStopId: Column<Int> = integer("bus_stop_id")
        .references(BusStopTable.id)
}

class RouteEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RouteEntity>(RouteTable)

    var routes by RouteListEntity referencedOn RouteTable.routesId
    var busStop by BusStopEntity referencedOn RouteTable.busStopId
}