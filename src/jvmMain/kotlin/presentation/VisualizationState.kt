package presentation

import center.sciprog.maps.compose.MapTileProvider
import center.sciprog.maps.compose.OpenStreetMapTileProvider
import domain.poko.BusStop
import domain.poko.BusStops
import domain.poko.Route
import domain.poko.RouteList
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import java.nio.file.Path

data class VisualizationState(
    val routes: Map<RouteList, List<Route>> = emptyMap(),
    val busStops: Map<BusStops, List<BusStop>> = emptyMap(),
    val routesShown: List<RouteList> = emptyList(),
    val busStopsKey: BusStops? = null,
    val routeListKey: RouteList? = null,
    val isBusStopsAddMode: Boolean = false,
    val isAddRoutesMode: Boolean = false,
    val mapTileProvider: MapTileProvider = OpenStreetMapTileProvider(
        client = HttpClient(CIO),
        cacheDirectory = Path.of("mapCache")
    ),
)

data class AddRoutesState(
    val numberOfRoutes: Int = 0,
)

fun <K, V> Map<K, List<V>>.myGet(id: Int, getId: K.() -> Int): List<V> {
    val key = keys.first { it.getId() == id }
    return getOrDefault(key, emptyList())
}
