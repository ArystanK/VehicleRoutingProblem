import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import center.sciprog.maps.compose.*
import center.sciprog.maps.features.FeatureGroup
import center.sciprog.maps.features.color
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
    val routes = Database.getAllRoutes()
    val busStops = Database.getBusStops()
    val points = routes.entries.map { it.value.map { index -> busStops.first { it.id == index } } }
    MaterialTheme {
        MapView(
            viewScope = viewScope,
            features = FeatureGroup.build(WebMercatorSpace) {
                points.forEach {
                    var prevPoint: Pair<Double, Double>? = null
                    it.map { it.lat to it.lon }.forEach { point ->
                        circle(point)
                        prevPoint?.let {
                            line(it, point)
                        }
                        prevPoint = point
                    }
//                    points(points = it.map { it.lat to it.lon }, id = it.toString())
                }
            }
        )
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}


