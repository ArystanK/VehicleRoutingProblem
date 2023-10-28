package presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import presentation.VisualizationComponent
import presentation.VisualizationIntent
import presentation.VisualizationState

@Composable
fun Menu(state: VisualizationState, component: VisualizationComponent) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        Box(
            modifier = Modifier
                .width(400.dp)
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
}