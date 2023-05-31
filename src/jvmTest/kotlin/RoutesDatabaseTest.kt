import data.database.VRPDatabase
import domain.poko.Route
import domain.repository.RoutesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RoutesDatabaseTest {
    @Test
    fun getRoutesTest() {
        println(VRPDatabase.getAllRoutes().first())
    }

    @Test
    fun getRoutesRepositoryTest(): Unit = runBlocking {
        val repository = RoutesRepository()
        repository.getRoutesList().onSuccess { routes: List<Route> ->
            repository.getRoutes().onSuccess { locations ->
                println(routes.groupBy { it.busStops })
                println(locations.groupBy { it.routes })
            }
        }
    }

}