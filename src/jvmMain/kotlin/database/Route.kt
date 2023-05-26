package database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

// Entity for the route table
object Route : IntIdTable("route") {
    val busStopId = reference("bus_stop_id", BusStopTable)
    val routeId = reference("route_id", RoutesList)
}

class RouteEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RouteEntity>(Route)

    var route by RoutesListEntity referencedOn Route.routeId
    var busStop by BusStopEntity referencedOn Route.busStopId
}