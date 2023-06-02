package presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
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
    isLoading: Boolean,
) {
    Dialog(
        onCloseRequest = onCloseRequest,
        visible = dialogShown,
        title = "Create routes"
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                TextField(
                    value = numberOfRoutes,
                    onValueChange = { onNumberOfRoutesChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                SolutionMethod.values().forEach {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(it.name)
                        RadioButton(
                            selected = solutionMethod == it,
                            onClick = {
                                onSolutionMethodChange(it)
                            }
                        )
                    }
                }
                Button(onClick = {
                    onSubmit()
                }) {
                    Text("Submit")
                }
            }
            if (isLoading) CircularProgressIndicator()
        }
    }
}
