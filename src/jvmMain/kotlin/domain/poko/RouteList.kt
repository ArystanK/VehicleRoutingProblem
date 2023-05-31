package domain.poko

import data.database.entities.RouteListEntity

fun RouteListEntity.toRouteList() = RouteList(
    id = id.value,
    solutionMethod = type.toSolutionMethod(),
    busStops = busStops.toBusStops()
)

data class RouteList(val id: Int, val solutionMethod: SolutionMethod, val busStops: BusStops)