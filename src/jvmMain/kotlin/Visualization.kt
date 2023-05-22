import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import center.sciprog.maps.compose.MapViewScope
import center.sciprog.maps.compose.OpenStreetMapTileProvider
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
    val fitness = Database.getFitness()
    val maxAvgFitness = fitness.maxBy { it.first }.first
    val maxMaxFitness = fitness.maxBy { it.second }.second
    val p = letsPlot(mapOf("average fitness" to fitness.map { it.first }))
    (p + ggsize(700, 350)).show()
//    MaterialTheme {
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            val normFitness =
//                fitness.map { (it.first / maxAvgFitness * 2 * size.height - 50) to (it.second / maxMaxFitness * 2 * size.height - 50) }
//            val normAvgFitness = normFitness.map { it.first }
//            val normMaxFitness = normFitness.map { it.second }
//            drawPoints(
//                points = normAvgFitness.withIndex().map {
//                    Offset(
//                        it.index.toFloat() / normAvgFitness.size * size.width + 20,
//                        it.value.toFloat() - 2 * center.y
//                    )
//                },
//                color = Color.Red,
//                pointMode = PointMode.Polygon
//            )
//            drawPoints(
//                points = normMaxFitness.withIndex().map {
//                    Offset(
//                        it.index.toFloat() / normMaxFitness.size * size.width + 20,
//                        it.value.toFloat() - 2 * center.y
//                    )
//                },
//                color = Color.Blue,
//                pointMode = PointMode.Polygon
//            )
//        }
////        MapView(
////            viewScope = viewScope,
////            features = FeatureGroup.build(WebMercatorSpace) {
////
////            }
////        )
//    }
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