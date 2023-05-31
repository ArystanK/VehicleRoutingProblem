package presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun <T> PickItemTable(
    items: List<T>,
    item: T?,
    onItemChange: (T) -> Unit,
    id: T.() -> Int,
    onAdd: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(60.dp),
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.Top,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            count = items.size,
            key = { items[it].id() }
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
                Button(
                    onClick = { onItemChange(items[it]) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (items[it] == item) Color.Gray else Color.Blue,
                        contentColor = Color.White
                    ),
                    shape = CircleShape,
                ) {
                    Text(items[it].id().toString(), fontSize = 20.sp)
                }
            }
        }
        item {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue, contentColor = Color.White),
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "add")
                }
            }
        }
    }
}