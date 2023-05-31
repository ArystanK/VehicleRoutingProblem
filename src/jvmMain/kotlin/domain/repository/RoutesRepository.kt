package domain.repository

import data.database.*
import domain.poko.BusStop
import domain.poko.BusStops
import domain.poko.Location
import domain.poko.Route
import kotlinx.coroutines.coroutineScope

class RoutesRepository {
//    suspend fun safeSolution(routes: List<List<Int>>, busStops: BusStops, type: String): Result<List<List<Location>>> =
//        coroutineScope {
//            val routesEntity = VRPDatabase.createRouteList(busStops, type)
//            val busStopList = VRPDatabase.getAllBusStops()
//            Result.success(routes.indices.map { i ->
//                VRPDatabase.createMultipleRoutes(
//                    busStops = routes[i].map { id -> busStopList.first { it.id == id } },
//                    routesList = routesEntity
//                )
//            })
//        }

    suspend fun safeSolution(
        routes: List<List<BusStop>>,
        busStops: BusStops,
        type: String,
    ): Result<List<List<Location>>> =
        Result.success(routes.indices.map { i ->
            val routesEntity = VRPDatabase.createRouteList(busStops, type)

            VRPDatabase.createMultipleRoutes(
                busStops = routes[i],
                routesList = routesEntity
            )
        })


    suspend fun getRoutes(): Result<List<Location>> = coroutineScope { Result.success(VRPDatabase.getAllRoutes()) }
    suspend fun getRouteById(id: Int): Result<Route> = coroutineScope {
        VRPDatabase.getRouteListById(id)?.let { Result.success(it) }
            ?: Result.failure(Exception("RouteList with id = $id not fount"))
    }

    suspend fun getRoutesList(): Result<List<Route>> = coroutineScope { Result.success(VRPDatabase.getAllRouteLists()) }

}