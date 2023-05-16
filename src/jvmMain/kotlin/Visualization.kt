import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import center.sciprog.maps.compose.*
import center.sciprog.maps.features.FeatureGroup
import io.ktor.client.*
import io.ktor.client.engine.cio.*
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
    MaterialTheme {
        MapView(
            viewScope = viewScope,
            features = FeatureGroup.build(WebMercatorSpace) {

            }
        )
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
