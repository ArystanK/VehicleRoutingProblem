package domain.repository

import data.BusStop
import data.client_api.BusStopsClient
import data.client_api.Feature
import data.client_api.Point
import data.client_api.Rectangle
import data.database.BusStopEntity
import data.database.VRPDatabase
import filterClose
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map

class BusStopsRepository {
    suspend fun getBusStops(
        searchBox: Rectangle = Rectangle(
            Point(71.19865356830648, 51.28193274061607),
            Point(71.65595628500706, 50.996389716773805)
        ),
    ): List<BusStopEntity> {
        val data = VRPDatabase.getAllBusStops()
        if (data.isNotEmpty()) return data
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

        val busStops = busStopFeatures.mapIndexed { index, feature ->
            BusStop(
                lat = feature.geometry.coordinates.last(),
                lon = feature.geometry.coordinates.first(),
                address = feature.properties.CompanyMetaData.address,
                id = index
            )
        }
        val busStopsEntity = VRPDatabase.createBusStops()
        return VRPDatabase.createMultipleBusStops(
            busStops = busStops,
            busStopsId = busStopsEntity.id.value
        )
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

