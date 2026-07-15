package com.example.ozonpricetracking.feature.home.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ozonpricetracking.core.products.domain.model.OzonProductInfo
import com.example.ozonpricetracking.core.theme.OzonPriceTrackingTheme
import com.example.ozonpricetracking.core.utils.PreviewData

@Composable
fun ListOfProducts(
    products: List<OzonProductInfo>,
    onClick: (id: Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(top = 8.dp)
    ) {
        items(
            items = products,
            key = { it.id }
        ) { product ->
            ProductCard(
                product,
                onClick
            )
        }
    }
}

@Preview(showBackground = true, name = "Светлая тема")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Темная тема")
@Composable
private fun ListOfProductsPreview() {
    OzonPriceTrackingTheme {
        Surface {
            ListOfProducts(
                products = PreviewData.products,
                onClick = {}
            )
        }
    }
}
