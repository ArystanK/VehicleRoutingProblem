package presentation

import center.sciprog.maps.compose.MapTileProvider
import center.sciprog.maps.compose.OpenStreetMapTileProvider
import data.database.RouteEntity
import data.database.RouteListEntity
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import java.nio.file.Path

//TODO: add customization
data class VisualizationState(
    val routes: Map<RouteListEntity, List<RouteEntity>> = emptyMap(),
    val routesShown: List<Int> = emptyList(),
    val busStopId: Int = 0,
    val mapTileProvider: MapTileProvider = OpenStreetMapTileProvider(
        client = HttpClient(CIO),
        cacheDirectory = Path.of("mapCache")
    ),
)