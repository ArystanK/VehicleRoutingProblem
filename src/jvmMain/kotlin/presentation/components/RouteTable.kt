package presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import domain.poko.BusStop
import domain.poko.Location

@Composable
fun RouteTable(
    modifier: Modifier = Modifier,
    routes: List<List<BusStop>>,
    shownRoutes: List<List<BusStop>>,
    onRouteSelected: (List<BusStop>) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(60.dp),
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.Top,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        items(routes.size) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
                Button(
                    onClick = { onRouteSelected(routes[it]) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = if (routes[it] in shownRoutes) Color.Gray else Color.Blue)
                ) {
                    Text(it.toString())
                }
            }
        }
    }
}

