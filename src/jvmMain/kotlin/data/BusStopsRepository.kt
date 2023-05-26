package data

import client_api.BusStopsClient
import client_api.Feature
import client_api.Point
import client_api.Rectangle
import database.Database
import filterClose
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map

class BusStopsRepository {
    private val client = BusStopsClient
    private val database = Database
    suspend fun getBusStops(searchBox: Rectangle): List<BusStop> {
        val data = database.getBusStops()
            .sortedBy { it.id }
        if (data.isNotEmpty()) return data
        val busStopFeatures = searchBox.partition(3)
            .asFlow()
            .buffer()
            .map { rectangle -> client.getBusStops(rectangle).features.distinctBy { it.geometry.coordinates } }
            .fold(emptyList<Feature>()) { accumulator, value -> accumulator + value }
            .distinct()
            .filterClose(
                epsilon = 0.005,
                getX = { it.geometry.coordinates.first() },
                getY = { it.geometry.coordinates.last() }
            )

        val busStops = busStopFeatures.mapIndexed { index, feature ->
            BusStop(
                lat = feature.geometry.coordinates.last(),
                lon = feature.geometry.coordinates.first(),
                address = feature.properties.CompanyMetaData.address,
                id = index
            )
        }
        database.saveBusStops(busStops)
        return busStops
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

