package domain.poko

import data.database.entities.RouteEntity

fun RouteEntity.toRoute() = Route(
    id = id.value,
    routes = routes.toRouteList(),
    busStop = busStop.toBusStop()
)

data class Route(val id: Int, val routes: RouteList, val busStop: BusStop) {
    fun toLocationPair() = busStop.lat to busStop.lon

}