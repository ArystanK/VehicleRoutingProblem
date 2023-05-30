package presentation.components

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.database.entities.BusStops

@Composable
fun BusStopsCollectionTable(
    busStops: List<BusStops>,
    busStopsId: Int,
    onBusStopIdChange: (Int) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(20.dp),
    ) {
        items(busStops.size) {
            Button(
                onClick = { onBusStopIdChange(it) },
                colors = ButtonDefaults.buttonColors(backgroundColor = if (it == busStopsId) Color.Gray else Color.Blue)
            ) {
                Text(busStops[it].id.toString())
            }
        }
    }
}