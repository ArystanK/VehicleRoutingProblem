package data.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object RouteTable : IntIdTable("route") {
    val routes = reference("routes_id", RouteListTable.id)
    val busStop = reference("bus_stop_id", BusStopTable.id)
}

class RouteEntity(id: EntityID<Int>) : IntEntity(id) {

    constructor(id: Int) : this(EntityID(id, RouteTable))

    companion object : IntEntityClass<RouteEntity>(RouteTable)

    var routes by RouteListEntity referencedOn RouteTable.routes
    var busStop by BusStopEntity referencedOn RouteTable.busStop
}

