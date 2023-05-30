package domain.repository

import data.database.RouteEntity
import data.database.VRPDatabase
import kotlinx.coroutines.coroutineScope

class RoutesRepository {
    suspend fun safeSolution(routes: List<List<Int>>, busStopsId: Int): List<List<RouteEntity>> = coroutineScope {
        routes.indices.map { i ->
            val routesEntity = VRPDatabase.createRouteList(busStopsId)
            VRPDatabase.createMultipleRoutes(busStopIds = routes[i], routesListId = routesEntity.id.value)
        }
    }

    suspend fun getRoutes(): List<RouteEntity> = coroutineScope { VRPDatabase.getAllRoutes() }

}