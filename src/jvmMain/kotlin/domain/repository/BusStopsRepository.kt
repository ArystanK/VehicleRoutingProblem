package domain.repository

import Point
import Rectangle
import data.client_api.BusStopsClient
import data.client_api.Feature
import data.database.VRPDatabase
import data.database.entities.BusStops
import domain.AstanaArea
import domain.numberOfBusStopsInAstana
import domain.poko.BusStop
import filterClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map

class BusStopsRepository {
    private val database = VRPDatabase
    suspend fun getBusStops(searchBox: Rectangle = AstanaArea): Result<List<BusStop>> = coroutineScope {
        val groupedBusStops = database.getAllBusStops().groupBy { it.busStops }
        val busStopsKey = BusStops(
            startSearchBoxLatitude = searchBox.a.x,
            startSearchBoxLongitude = searchBox.a.y,
            endSearchBoxLatitude = searchBox.b.x,
            endSearchBoxLongitude = searchBox.b.y
        )
        val busStopsKeyOfAstana = BusStops(
            startSearchBoxLatitude = AstanaArea.a.x,
            startSearchBoxLongitude = AstanaArea.a.y,
            endSearchBoxLatitude = AstanaArea.b.x,
            endSearchBoxLongitude = AstanaArea.b.y
        )
        val allBusStops = groupedBusStops[busStopsKeyOfAstana] ?: return@coroutineScope requestFromApi(searchBox)
        if (allBusStops.size != numberOfBusStopsInAstana) return@coroutineScope requestFromApi(searchBox)
        val busStopsInSearchBox = groupedBusStops[busStopsKey]
        if (busStopsInSearchBox == null) {
            val busStops = database.createBusStops(searchBox)
            val busStopsInSearchRegion =
                allBusStops.filter { it.toLocationPair() in searchBox }.map { it.copy(busStops = busStops) }
            return@coroutineScope Result.success(database.createMultipleBusStops(busStopsInSearchRegion))
        }

        Result.success(busStopsInSearchBox)
    }

    private suspend fun requestFromApi(searchBox: Rectangle): Result<List<BusStop>> = coroutineScope {
        val busStopFeatures = searchBox.partition(3)
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

        val busStopsEntity = database.createBusStops(searchBox)
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

    private fun Rectangle.partition(n: Int): List<Rectangle> {
        val width = (b.x - a.x) / n
        val height = (b.y - a.y) / n
        return List(n) {
            val startX = a.x + width * it
            List(n) {
                val startY = a.y + height * it
                Rectangle(Point(startX, startY), Point(startX + width, startY + height))
            }
        }.flatten()
    }
}

