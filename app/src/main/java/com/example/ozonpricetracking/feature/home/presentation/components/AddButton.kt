package com.example.ozonpricetracking.feature.home.presentation.components

import android.content.res.Configuration
import android.view.Surface
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ozonpricetracking.core.theme.OzonPriceTrackingTheme

@Composable
fun AddButton(onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.Add, "Add")
    }
}

@Preview(showBackground = true, name = "Светлая тема")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Темная тема")
@Composable
private fun AddButtonPreview(){
    OzonPriceTrackingTheme {
        Surface {
            AddButton(onClick = {})
        }
    }
}
