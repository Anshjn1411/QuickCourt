package com.project.odoo_235.presentation.screens.user.screen.MianScreen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReviewDialog(
    courtId: String,
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onSubmitted: () -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    val submitting by viewModel.reviewSubmitting.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Review") },
        text = {
            Column {
                OutlinedTextField(value = rating.toString(), onValueChange = { rating = it.toIntOrNull() ?: 5 }, label = { Text("Rating (1-5)") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = comment, onValueChange = { comment = it }, label = { Text("Comment") })
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.submitCourtReview(courtId, rating.coerceIn(1,5), comment.ifBlank { null }) { ok, msg ->
                    if (ok) onSubmitted() else {/* show toast/snack with msg */}
                }
            }, enabled = !submitting) { Text(if (submitting) "Submitting..." else "Submit") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}