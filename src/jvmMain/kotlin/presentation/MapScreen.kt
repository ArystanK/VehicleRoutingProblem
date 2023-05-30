package presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import center.sciprog.maps.compose.*
import center.sciprog.maps.features.FeatureGroup
import center.sciprog.maps.features.color
import data.database.entities.BusStops
import domain.repository.BusStopsRepository
import domain.repository.RoutesRepository
import presentation.components.BusStopsCollectionTable
import presentation.components.RouteTable

@Composable
fun App(component: VisualizationComponent) {
    val state by component.state.collectAsState()

    val viewScope = MapViewScope.remember(state.mapTileProvider)

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {

            MapView(
                viewScope = viewScope,
                features = FeatureGroup.build(WebMercatorSpace) {
                    state.routesShown.forEach { route ->
                        state.routes[route]?.let {
                            multiLine(it.map { it.toLocationPair() }).color(Color.Blue)
                        }
                    }

                    state.busStops[BusStops(state.busStopsId, 0.0, 0.0, 0.0, 0.0)]
                        ?.forEach {
                            circle(it.lat to it.lon)
                        }
                }
            )
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier
                    .width(1000.dp)
                    .fillMaxHeight()
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    BusStopsCollectionTable(
                        busStops = state.busStops.keys.toList(),
                        busStopsId = state.busStopsId,
                        onBusStopIdChange = { component.reduce(VisualizationIntent.BusStopsPickIntent(it)) },
                    )
                    RouteTable(
                        routes = state.routes.getOrDefault(state.routeListId, emptyList()),
                        onRouteSelected = { component.reduce(VisualizationIntent.RouteShowIntent(it)) },
                        shownRoutes = state.routesShown
                    )
                }
            }
        }
    }
}


fun main() {
    val busStopsRepository = BusStopsRepository()
    val routesRepository = RoutesRepository()
    val component = VisualizationComponent(routesRepository, busStopsRepository)
    application {
        Window(onCloseRequest = ::exitApplication) {
            App(component)
        }
    }
}


