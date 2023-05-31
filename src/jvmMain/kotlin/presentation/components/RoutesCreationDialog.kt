package presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import domain.poko.SolutionMethod

@Composable
fun RoutesCreationDialog(
    dialogShown: Boolean,
    numberOfRoutes: String,
    onNumberOfRoutesChange: (String) -> Unit,
    solutionMethod: SolutionMethod,
    onSolutionMethodChange: (SolutionMethod) -> Unit,
    onCloseRequest: () -> Unit,
    onSubmit: () -> Unit,
) {
    Dialog(
        onCloseRequest = onCloseRequest,
        visible = dialogShown,
        title = "Create routes"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = numberOfRoutes,
                onValueChange = { onNumberOfRoutesChange(it) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            SolutionMethod.values().forEach {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(it.name)
                    RadioButton(
                        selected = solutionMethod == it,
                        onClick = { onSolutionMethodChange(it) }
                    )
                }
            }
            Button(onClick = onSubmit) {
                Text("Submit")
            }
        }
    }
}
