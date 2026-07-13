package com.example.ozonpricetracking.feature.home.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun AddButton(onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.Add, "Add")
    }
}
