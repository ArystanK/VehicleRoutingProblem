package presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import domain.poko.Route
import domain.poko.RouteList

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RouteTable(
    modifier: Modifier = Modifier,
    routes: List<Route>,
    shownRoutes: List<RouteList>,
    onRouteSelected: (Int) -> Unit,
) {
    val selectedRoutes = routes.filter { it.routes in shownRoutes }
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(10),
        modifier = modifier,
        contentPadding = PaddingValues(4.dp)
    ) {
        items(routes.size) {
            Button(
                onClick = { onRouteSelected(it) },
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = if (routes[it] in selectedRoutes) Color.Gray else Color.Blue)
            ) {
                Text(it.toString())
            }
        }
    }
}

