package presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import center.sciprog.maps.compose.*
import center.sciprog.maps.features.FeatureGroup
import center.sciprog.maps.features.color
import domain.repository.BusStopsRepository
import domain.repository.RoutesRepository

@Composable
fun App(component: VisualizationComponent) {
    val state by component.state.collectAsState()

    val viewScope = MapViewScope.remember(state.mapTileProvider)

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {

            MapView(
                viewScope = viewScope,
                features = FeatureGroup.build(WebMercatorSpace) {
//                    state.routesShown.forEach { route ->
//                        multiLine(route).color(Color.Blue)
//                    }

//                    state.busStops.forEach {
//                        circle(it.lat to it.lon)
//                    }
                }
            )
//            RouteTable(
//                routes = state.routes,
//                onRouteSelected = { component.reduce(VisualizationIntent.RouteShowIntent(it)) },
//                modifier = Modifier.align(Alignment.TopEnd),
//                shownRoutes = state.routesShown
//            )
        }
    }
}


fun main() {
    val busStopsRepository = BusStopsRepository()
    val routesRepository = RoutesRepository()
    val component = VisualizationComponent(routesRepository)
    application {
        Window(onCloseRequest = ::exitApplication) {
            App(component)
        }
    }
}


