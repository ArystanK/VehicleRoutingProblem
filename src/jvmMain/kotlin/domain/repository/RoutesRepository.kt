package domain.repository

import data.database.*
import data.database.entities.*
import domain.poko.Route
import kotlinx.coroutines.coroutineScope

class RoutesRepository {
    suspend fun safeSolution(routes: List<List<Int>>, busStops: BusStops): Result<List<List<Route>>> = coroutineScope {
        Result.success(routes.indices.map { i ->
            val routesEntity = VRPDatabase.createRouteList(busStops)
            val busStopList = VRPDatabase.getAllBusStops()
            VRPDatabase.createMultipleRoutes(
                busStops = routes[i].map { id -> busStopList.first { it.id == id } },
                routesList = routesEntity
            )
        })
    }

    suspend fun safeSolution(routes: List<List<Int>>, busStopsId: Int): Result<List<List<Route>>> = coroutineScope {
        Result.success(routes.indices.map { i ->
            val busStops = VRPDatabase.getBusStopsById(busStopsId)
                ?: return@coroutineScope Result.failure(Exception("Bus stops is not find"))
            val routesEntity = VRPDatabase.createRouteList(busStops)
            val busStopList = VRPDatabase.getAllBusStops()
            VRPDatabase.createMultipleRoutes(
                busStops = routes[i].map { id -> busStopList.first { it.id == id } },
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