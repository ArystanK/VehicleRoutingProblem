package presentation

import center.sciprog.maps.compose.MapTileProvider
import center.sciprog.maps.compose.OpenStreetMapTileProvider
import domain.poko.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import java.nio.file.Path

data class VisualizationState(
    val routes: List<List<BusStop>> = emptyList(),
    val routesList: Map<BusStops, List<Route>> = emptyMap(),
    val busStops: Map<BusStops, List<BusStop>> = emptyMap(),
    val routesShown: List<List<BusStop>> = emptyList(),
    val busStopsKey: BusStops? = null,
    val routeKey: Route? = null,
    val isBusStopsAddMode: Boolean = false,
    val isAddRoutesMode: Boolean = false,
    val addRoutesState: AddRoutesState = AddRoutesState(),
    val mapTileProvider: MapTileProvider = OpenStreetMapTileProvider(
        client = HttpClient(CIO),
        cacheDirectory = Path.of("mapCache")
    ),
)

data class AddRoutesState(
    val numberOfRoutes: String = "",
    val solutionMethod: SolutionMethod = SolutionMethod.LP,
)

