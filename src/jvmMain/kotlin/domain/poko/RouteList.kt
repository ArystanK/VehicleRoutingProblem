package domain.poko

import data.database.entities.BusStops
import data.database.entities.RouteListEntity
import data.database.entities.toBusStops

fun RouteListEntity.toRouteList() = RouteList(
    id = id.value,
    type = type,
    busStops = busStops.toBusStops()
)

data class RouteList(val id: Int, val type: String, val busStops: BusStops)