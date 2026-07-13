package com.example.ozonpricetracking.feature.createProduct.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CreateProductDialog(
    url: String?,
    onDismiss: () -> Unit,
    viewModel: CreateProductDialogViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var productUrl by remember(url) { mutableStateOf(url ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавление товара") },
        text = {
            Column {
                OutlinedTextField(
                    value = productUrl,
                    onValueChange = { productUrl = it },
                    label = { Text("Ссылка") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state !is AddProductUiState.Loading
                )

                val targetState =  state
                if (targetState is AddProductUiState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = targetState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                if (state is AddProductUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Button(
                    enabled = state !is AddProductUiState.Loading,
                    onClick = { viewModel.saveProduct(productUrl, onSuccess = onDismiss) },
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Сохранить")
                }
            }
        }
        ,
        dismissButton = {
            if (state !is AddProductUiState.Loading) {
                TextButton(onClick = onDismiss) {
                    Text("Отмена")
                }
            }
        }
    )
}
