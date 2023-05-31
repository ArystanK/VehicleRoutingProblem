package presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import center.sciprog.maps.features.ViewConfig
import center.sciprog.maps.features.color
import domain.repository.BusStopsRepository
import domain.repository.FitnessRepository
import domain.repository.RoutesRepository
import presentation.components.PickItemTable
import presentation.components.RouteTable
import presentation.components.RoutesCreationDialog

@Composable
fun App(component: VisualizationComponent) {
    val state by component.state.collectAsState()

    val viewScope = MapViewScope.remember(
        mapTileProvider = state.mapTileProvider,
        config = ViewConfig(
            onSelect = {
                println(it)
                if (state.isBusStopsAddMode)
                    component.reduce(VisualizationIntent.BusStopsAddIntent(it))
            },
            zoomOnSelect = false
        )
    )
    val features = FeatureGroup.build(WebMercatorSpace) {
        state.routesShown.forEach {
            multiLine(it.map { it.lat to it.lon }).color(Color.Blue)
        }

        state.busStopsKey?.let {
            state.busStops[it]
                ?.forEach {
                    circle(it.lat to it.lon)
                }
        }
    }

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            MapView(
                viewScope = viewScope,
                features = features,
            )
            Box(Modifier.fillMaxSize().background(Color.Gray.copy(alpha = if (state.isBusStopsAddMode) 0.7f else 0f)))
            Box(
                modifier = Modifier
                    .width(600.dp)
                    .fillMaxHeight()
                    .background(Color.White.copy(alpha = 0.8f)),
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        Box(
                            modifier = Modifier.background(Color.Gray, RoundedCornerShape(4.dp)).padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Bus stops:", style = MaterialTheme.typography.h5, color = Color.White)
                        }
                    }
                    PickItemTable(
                        items = state.busStops.keys.toList(),
                        item = state.busStopsKey,
                        onItemChange = { component.reduce(VisualizationIntent.BusStopsPickIntent(it)) },
                        id = { id },
                        onAdd = { component.reduce(VisualizationIntent.BusStopsAddModeToggle) }
                    )

                    Box(modifier = Modifier.padding(8.dp)) {
                        Box(
                            modifier = Modifier.background(Color.Gray, RoundedCornerShape(4.dp)).padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Routes:", style = MaterialTheme.typography.h5, color = Color.White)
                        }
                    }
                    PickItemTable(
                        items = state.routesList[state.busStopsKey] ?: emptyList(),
                        item = state.routeKey,
                        onItemChange = { component.reduce(VisualizationIntent.RouteListPickIntent(it)) },
                        id = { id },
                        onAdd = { component.reduce(VisualizationIntent.RoutesAddModeToggle) }
                    )
                    Box(modifier = Modifier.padding(8.dp)) {
                        Box(
                            modifier = Modifier.background(Color.Gray, RoundedCornerShape(4.dp)).padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Route:", style = MaterialTheme.typography.h5, color = Color.White)
                        }
                    }
                    RouteTable(
                        routes = state.routes,
                        onRouteSelected = { component.reduce(VisualizationIntent.RouteShowIntent(it)) },
                        shownRoutes = state.routesShown
                    )
                }
            }
        }
        RoutesCreationDialog(
            dialogShown = state.isAddRoutesMode,
            numberOfRoutes = state.addRoutesState.numberOfRoutes,
            solutionMethod = state.addRoutesState.solutionMethod,
            onSolutionMethodChange = { component.reduce(VisualizationIntent.SolutionMethodChangeIntent(it)) },
            onNumberOfRoutesChange = { component.reduce(VisualizationIntent.NumberOfRouteChangeIntent(it)) },
            onCloseRequest = { component.reduce(VisualizationIntent.RoutesAddModeToggle) },
            onSubmit = {
                component.reduce(VisualizationIntent.RoutesAdd)
            }
        )
    }
}


fun main() {
    val busStopsRepository = BusStopsRepository()
    val routesRepository = RoutesRepository()
    val fitnessRepository = FitnessRepository()
    val component = VisualizationComponent(routesRepository, busStopsRepository, fitnessRepository)
    application {
        Window(onCloseRequest = ::exitApplication, title = "VRP") {
            App(component)
        }
    }
}


