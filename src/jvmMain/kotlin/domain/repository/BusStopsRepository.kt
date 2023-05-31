package domain.repository

import center.sciprog.maps.compose.WebMercatorSpace.Rectangle
import center.sciprog.maps.coordinates.Gmc
import center.sciprog.maps.features.Rectangle
import data.client_api.BusStopsClient
import data.client_api.Feature
import data.database.VRPDatabase
import domain.AstanaArea
import domain.numberOfBusStopsInAstana
import domain.poko.BusStop
import domain.poko.BusStops
import domain.poko.toBusStops
import filterClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import space.kscience.kmath.geometry.Degrees

class BusStopsRepository {
    private val database = VRPDatabase

    suspend fun getAllBusStops(): Result<List<BusStop>> = coroutineScope {
        Result.success(database.getAllBusStops())
    }

    suspend fun getBusStops(searchBox: Rectangle<Gmc> = AstanaArea): Result<List<BusStop>> = coroutineScope {
        val groupedBusStops = database.getAllBusStops().groupBy { it.busStops }
        val busStopsKey = database.getBusStopsBySearchBox(searchBox)
        val busStopsKeyOfAstana = database.getBusStopsBySearchBox(AstanaArea)
        val allBusStops =
            groupedBusStops[busStopsKeyOfAstana]?.distinctBy { it.id }
                ?: return@coroutineScope requestFromApi()
        if (allBusStops.size < numberOfBusStopsInAstana) return@coroutineScope requestFromApi()
        val busStopsInSearchBox = groupedBusStops[busStopsKey]
        if (busStopsInSearchBox == null) {
            val busStops = database.createBusStops(searchBox)
            val busStopsInSearchRegion =
                allBusStops.filter { it.toGmc() in searchBox }.map { it.copy(busStops = busStops) }
            return@coroutineScope Result.success(database.createMultipleBusStops(busStopsInSearchRegion))
        }

        Result.success(busStopsInSearchBox)
    }

    suspend fun addBusStops(searchBox: Rectangle<Gmc>): Result<List<BusStop>> = coroutineScope {
        val busStops = database.createBusStops(searchBox)
        val allBusStopsGrouped = database.getAllBusStops().groupBy { it.busStops }
        val allBusStopsKey = database.getBusStopsBySearchBox(AstanaArea)
        val allBusStops = allBusStopsGrouped[allBusStopsKey] ?: return@coroutineScope requestFromApi()
        val busStopsInSearchBox = allBusStops.filter { it.toGmc() in searchBox }.map { it.copy(busStops = busStops) }
        return@coroutineScope Result.success(database.createMultipleBusStops(busStopsInSearchBox))
    }

    private suspend fun requestFromApi(): Result<List<BusStop>> = coroutineScope {
        val busStopFeatures = AstanaArea.partition(3)
            .asFlow()
            .buffer()
            .map { rectangle -> BusStopsClient.getBusStops(rectangle).features.distinctBy { it.geometry.coordinates } }
            .fold(emptyList<Feature>()) { accumulator, value -> accumulator + value }
            .distinct()
            .filterClose(
                epsilon = 0.005,
                getX = { it.geometry.coordinates.first() },
                getY = { it.geometry.coordinates.last() }
            )

        val busStopsEntity = database.createBusStops(AstanaArea)
        val busStops = busStopFeatures.mapIndexed { index, feature ->
            BusStop(
                lat = feature.geometry.coordinates.last(),
                lon = feature.geometry.coordinates.first(),
                address = feature.properties.CompanyMetaData.address,
                id = index,
                busStops = busStopsEntity
            )
        }
        Result.success(database.createMultipleBusStops(busStops = busStops))
    }

    private fun Rectangle<Gmc>.partition(n: Int): List<Rectangle<Gmc>> {
        val width = (b.latitude.toDegrees().value - a.latitude.toDegrees().value) / n
        val height = (b.longitude.toDegrees().value - a.longitude.toDegrees().value) / n
        return List(n) {
            val startX = a.latitude.toDegrees().value + width * it
            List(n) {
                val startY = a.longitude.toDegrees().value + height * it
                Rectangle(Gmc(Degrees(startX), Degrees(startY)), Gmc(Degrees(startX + width), Degrees(startY + height)))
            }
        }.flatten()
    }

    suspend fun getBusStopsBySearchBox(searchBox: Rectangle<Gmc>): Result<BusStops> = coroutineScope {
        database.getBusStopsBySearchBox(searchBox)?.let { Result.success(it) }
            ?: Result.failure(Exception("Cannot find bus stops"))
    }

    suspend fun getBusStopsById(id: Int): Result<BusStops> = coroutineScope {
        database.getBusStopsById(id)?.let { Result.success(it) }
            ?: Result.failure(Exception("Cannot find bus stops"))
    }
}

