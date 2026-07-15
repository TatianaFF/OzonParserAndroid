package com.example.ozonpricetracking.feature.details.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Удалить?")
        },
        text = {
            Text(text = "Вы уверены, что хотите удалить этот товар из списка отслеживания?")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(text = "Удалить", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Отмена", color = Color.Gray)
            }
        }
    )
}
