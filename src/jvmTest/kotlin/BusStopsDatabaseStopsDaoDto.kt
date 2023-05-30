import data.database.VRPDatabase
import domain.repository.BusStopsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test

class BusStopsDatabaseStopsDaoDto {
    @Test
    fun getBusStopsTest() {
        println(VRPDatabase.getAllBusStops())
    }

    @Test
    fun getBusStopsRepositoryTest() = runBlocking {
        val repository = BusStopsRepository()
        val bs = repository.getBusStops()
    }
}