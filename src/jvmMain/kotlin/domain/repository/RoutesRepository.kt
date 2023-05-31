package domain.repository

import data.database.*
import data.database.entities.*
import domain.poko.BusStop
import domain.poko.BusStops
import domain.poko.Route
import domain.poko.RouteList
import kotlinx.coroutines.coroutineScope

class RoutesRepository {
    suspend fun safeSolution(routes: List<List<Int>>, busStops: BusStops, type: String): Result<List<List<Route>>> =
        coroutineScope {
            val routesEntity = VRPDatabase.createRouteList(busStops, type)
            val busStopList = VRPDatabase.getAllBusStops()
            Result.success(routes.indices.map { i ->
                VRPDatabase.createMultipleRoutes(
                    busStops = routes[i].map { id -> busStopList.first { it.id == id } },
                    routesList = routesEntity
                )
            })
        }

    suspend fun safeSolution(
        routes: List<List<Int>>,
        busStopsId: Int,
        type: String,
        busStopList: List<BusStop>,
    ): Result<List<List<Route>>> =
        coroutineScope {
            val busStops = VRPDatabase.getBusStopsById(busStopsId)
                ?: return@coroutineScope Result.failure(Exception("Bus stops is not find"))
            val routesEntity = VRPDatabase.createRouteList(busStops, type)

            Result.success(routes.indices.map { i ->
                VRPDatabase.createMultipleRoutes(
                    busStops = routes[i].map { id -> busStopList[id] },
                    routesList = routesEntity
                )
            })
        }

    suspend fun getRoutes(): Result<List<Route>> = coroutineScope { Result.success(VRPDatabase.getAllRoutes()) }
    suspend fun getRouteById(id: Int): Result<RouteList> = coroutineScope {
        VRPDatabase.getRouteListById(id)?.let { Result.success(it) }
            ?: Result.failure(Exception("RouteList with id = $id not fount"))
    }

}