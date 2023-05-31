package domain.poko

import data.database.entities.RouteListEntity

fun RouteListEntity.toRouteList() = Route(
    id = id.value,
    solutionMethod = type.toSolutionMethod(),
    busStops = busStops.toBusStops()
)

data class Route(val id: Int, val solutionMethod: SolutionMethod, val busStops: BusStops) {
    override fun toString(): String {
        return "Route(id = $id, solutionMethod = $solutionMethod, busStops = ${busStops.id})"
    }
}