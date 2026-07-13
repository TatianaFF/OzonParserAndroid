package com.example.ozonpricetracking.feature.home.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ozonpricetracking.core.products.domain.model.OzonProductInfo

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
