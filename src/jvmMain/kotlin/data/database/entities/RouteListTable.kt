package data.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

internal object RouteListTable : IntIdTable("routes") {
    val type: Column<String> = varchar("type", 3)
    val busStopsId = reference("bus_stops_id", BusStopsTable.id)
}

class RouteListEntity(id: EntityID<Int>) : IntEntity(id) {

    constructor(id: Int) : this(EntityID(id, RouteListTable))

    companion object : IntEntityClass<RouteListEntity>(RouteListTable)

    var type by RouteListTable.type
    var busStops by BusStopsEntity referencedOn RouteListTable.busStopsId
}

fun RouteListEntity.toRouteList() = RouteList(
    id = id.value,
    type = type,
    busStops = busStops.toBusStops()
)

data class RouteList(val id: Int, val type: String, val busStops: BusStops)