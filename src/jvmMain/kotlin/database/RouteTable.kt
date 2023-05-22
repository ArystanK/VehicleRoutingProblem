package database

import org.jetbrains.exposed.dao.id.IntIdTable

object RouteTable : IntIdTable("route") {
    val nodeId = integer("node_id")
        .references(BusStopTable.id)
    val routeId = integer("routes_id")
}
