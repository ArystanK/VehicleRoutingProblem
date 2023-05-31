import data.database.VRPDatabase
import domain.repository.BusStopsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test

class BusStopsDatabaseStopsDaoDto {
    @Test
    fun getBusStopsTest() {
        VRPDatabase.getAllBusStopCollections().associateWith {
            VRPDatabase.getBusStopsByBusStopsKey(it)
        }.forEach { println("${it.key} -> ${it.value.size}") }
    }

    @Test
    fun getBusStopsRepositoryTest() = runBlocking {
        val repository = BusStopsRepository()
        val bs = repository.getBusStops()
    }
}