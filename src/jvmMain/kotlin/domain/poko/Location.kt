package domain.poko

import data.database.entities.RouteEntity

fun RouteEntity.toRoute() = Location(
    id = id.value,
    routes = routes.toRouteList(),
    busStop = busStop.toBusStop()
)

data class Location(val id: Int, val routes: Route, val busStop: BusStop) {
    fun toLocationPair() = busStop.lat to busStop.lon

    override fun toString(): String {
        return "Location(id = $id, routes = ${routes.id}, busStop = ${busStop.id})"
    }

}