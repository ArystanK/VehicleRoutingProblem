package data

import database.Database
import kotlinx.coroutines.coroutineScope

class RoutesRepository(
    private val solution: VRPSolution,
    private val database: Database,
) {
    suspend fun safeSolution(source: String) {
        coroutineScope {
            val prevRouteId = database.getLastRouteId() + 1
            val routes = solution.toRoutes()
            for (i in prevRouteId until routes.size + prevRouteId) {
                val route = routes[i - prevRouteId]
                Database.saveRoute(route, i, source)
            }
        }
    }
}