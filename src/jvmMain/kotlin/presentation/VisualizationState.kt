package presentation

import center.sciprog.maps.compose.MapTileProvider
import center.sciprog.maps.compose.OpenStreetMapTileProvider
import data.database.entities.*
import domain.poko.BusStop
import domain.poko.Route
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import java.nio.file.Path

//TODO: add customization
data class VisualizationState(
    val routes: Map<RouteList, List<Route>> = emptyMap(),
    val busStops: Map<BusStops, List<BusStop>> = emptyMap(),
    val routesShown: List<RouteList> = emptyList(),
    val busStopsId: Int = -1,
    val routeListId: Int = -1,
    val mapTileProvider: MapTileProvider = OpenStreetMapTileProvider(
        client = HttpClient(CIO),
        cacheDirectory = Path.of("mapCache")
    ),
)

fun <K, V> Map<K, List<V>>.myGet(id: Int, getId: K.() -> Int): List<V> {
    val key = keys.first { it.getId() == id }
    return getOrDefault(key, emptyList())
}
