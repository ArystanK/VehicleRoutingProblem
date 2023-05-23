import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import center.sciprog.maps.compose.*
import center.sciprog.maps.features.FeatureGroup
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
                    points(it.map { it.lat to it.lon })
                }
            }
        )
    }
}

//fun main() = application {
//    Window(onCloseRequest = ::exitApplication) {
//        App()
//    }
//}


fun main() {
    val fitness = Database.getFitness()
    val data1 = mapOf(
        "average fitness" to fitness.map { it.first },
        "generation" to fitness.indices
    )

    val p1 =
        letsPlot(data1) + geomPoint(color = "red", alpha = .3) { x = "generation"; y = "average fitness" }
    val data2 = mapOf(
        "max fitness" to fitness.map { it.second },
        "generation" to fitness.indices
    )
    val p2 =
        letsPlot(data2) + geomPoint(color = "green", alpha = .3) { x = "generation"; y = "max fitness" }
    val bunch = GGBunch()
        .addPlot(p1, 0, 0)
        .addPlot(p2, 1000, 0)
    bunch.show()

//
//    val maxAvgFitness = fitness.maxBy { it.first }.first
//    val maxMaxFitness = fitness.maxBy { it.second }.second
//    val p = letsPlot(

//    )
//    (p + ggsize(700, 350)).show()
}