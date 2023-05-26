import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import center.sciprog.maps.compose.*
import center.sciprog.maps.features.FeatureGroup
import center.sciprog.maps.features.color
import data.BusStop
import database.Database
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import org.jetbrains.letsPlot.GGBunch
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.letsPlot
import java.nio.file.Path

@Composable
@Preview
fun App() {
    val mapTileProvider = remember {
        OpenStreetMapTileProvider(
            client = HttpClient(CIO),
            cacheDirectory = Path.of("mapCache")
        )
    }
    val viewScope = MapViewScope.remember(mapTileProvider)
    var routes: Map<Int, List<Int>>? by remember { mutableStateOf(null) }
    var busStops: List<BusStop>? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        routes = Database.getAllRoutes()
        busStops = Database.getBusStops()
    }
    if (routes == null || busStops == null) {
        CircularProgressIndicator()
    } else {
        val points = routes!!.entries.map { it.value.map { index -> busStops!!.first { it.id == index } } }
        var shownRoute by remember { mutableStateOf("") }
        val shownRouteIndex = try {
            val index = shownRoute.toInt()
            if (index in points.indices) index else 0
        } catch (e: Exception) {
            0
        }
        MaterialTheme {
            Column {
                TextField(
                    value = shownRoute,
                    onValueChange = {
                        shownRoute = it

                    }
                )
                MapView(
                    viewScope = viewScope,
                    features = FeatureGroup.build(WebMercatorSpace) {
                        points.getOrNull(shownRouteIndex)?.let {
                            var prevPoint: Pair<Double, Double>? = null
                            it.map { it.lat to it.lon }.forEach { point ->
                                prevPoint?.let {
                                    line(it, point).color(Color.Blue)
                                }
                                prevPoint = point
                            }
//                    points(points = it.map { it.lat to it.lon }, id = it.toString())
                        }
                        busStops!!.forEach {
                            circle(it.lat to it.lon)
                        }
                    }
                )
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}


