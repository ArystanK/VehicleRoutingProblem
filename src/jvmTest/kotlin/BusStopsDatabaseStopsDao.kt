import data.database.VRPDatabase
import domain.repository.BusStopsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test

class BusStopsDatabaseStopsDao {
    @Test
    fun getBusStopsTest() {
        println(VRPDatabase.getAllBusStops())
    }

    @Test
    fun getBusStopsRepositoryTest() = runBlocking {
        val repository = BusStopsRepository()
        println(repository.getBusStops())
    }
}