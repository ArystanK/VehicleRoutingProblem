package presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import center.sciprog.maps.compose.*
import center.sciprog.maps.features.FeatureGroup
import center.sciprog.maps.features.ViewConfig
import center.sciprog.maps.features.color
import presentation.components.Menu
import presentation.components.RoutesCreationDialog
import kotlin.math.sqrt

@Composable
fun App(component: VisualizationComponent) {
    val state by component.state.collectAsState()

    val viewScope = MapViewScope.remember(
        mapTileProvider = state.mapTileProvider,
        config = ViewConfig(
            onSelect = {
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
            state.busStops[it]?.forEach {
                circle(it.lat to it.lon)
            }
        }
    }

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MapView(
                viewScope = viewScope,
                features = features,
            )
            Box(Modifier.fillMaxSize().background(Color.Gray.copy(alpha = if (state.isBusStopsAddMode) 0.7f else 0f)))
            Menu(state, component)

            RoutesCreationDialog(
                dialogShown = state.isAddRoutesMode,
                numberOfRoutes = state.addRoutesState.numberOfRoutes,
                solutionMethod = state.addRoutesState.solutionMethod,
                onSolutionMethodChange = { component.reduce(VisualizationIntent.SolutionMethodChangeIntent(it)) },
                onNumberOfRoutesChange = { component.reduce(VisualizationIntent.NumberOfRouteChangeIntent(it)) },
                onCloseRequest = { component.reduce(VisualizationIntent.RoutesAddModeToggle) },
                onSubmit = { component.reduce(VisualizationIntent.RoutesAdd) },
                isLoading = state.isLoading
            )
        }
    }
}

private fun invSqrt(x: Double): Double {
    var i: Long
    val threehalfs = 1.5f
    val x2 = x * 0.5f
    var y = x
    i = java.lang.Double.doubleToLongBits(y) // evil floating point bit level hacking
    i = 0x5f3759df - (i shr 1) // what the fuck?
    y = java.lang.Double.longBitsToDouble(i)
    y *= (threehalfs - x2 * y * y) // 1st iteration
    // y = y * (threehalfs - (x2 * y * y)); // 2nd iteration, this can be removed
    return y
//        return (float) (1.0f / Math.sqrt(x));
}

fun main() {
    for (i in 2..10) {
        println(invSqrt(i.toDouble()))
        println(1 / (sqrt(i.toDouble())))
    }
//    val busStopsRepository = BusStopsRepository()
//    val routesRepository = RoutesRepository()
//    val fitnessRepository = FitnessRepository()
//    val component = VisualizationComponent(routesRepository, busStopsRepository, fitnessRepository)
//    application {
//        Window(onCloseRequest = ::exitApplication, title = "VRP") {
//            App(component)
//        }
//    }
}
