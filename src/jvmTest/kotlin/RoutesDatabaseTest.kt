import data.database.VRPDatabase
import domain.repository.BusStopsRepository
import domain.repository.RoutesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RoutesDatabaseTest {
    @Test
    fun getRoutesTest() {
        println(VRPDatabase.getAllRoutes().first())
    }

    @Test
    fun gerRoutesRepositoryTest() = runBlocking {
        val repository = RoutesRepository()
        println(repository.getRoutes())
    }

    @Test
    fun routesMapCheck(): Unit = runBlocking {
        val busStopsRepository = BusStopsRepository()
        val routesRepository = RoutesRepository()
        val busStops = busStopsRepository.getBusStops()
        val routes = routesRepository.getRoutes().map { it.busStop }
        println(routes)
    }
}