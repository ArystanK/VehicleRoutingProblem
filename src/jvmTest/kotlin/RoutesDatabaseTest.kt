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

}