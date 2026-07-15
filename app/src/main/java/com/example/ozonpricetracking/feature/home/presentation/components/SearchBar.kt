package com.example.ozonpricetracking.feature.home.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ozonpricetracking.core.products.domain.model.OzonProductInfo
import com.example.ozonpricetracking.core.theme.OzonPriceTrackingTheme
import com.example.ozonpricetracking.core.utils.PreviewData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    products: List<OzonProductInfo>,
    onNavigateToProduct: (id: Long) -> Unit,
    query: String,
    onChangeQuery: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchSuggestions = remember(query, products) {
        if (query.isBlank()) {
            emptyList()
        } else {
            products
                .filter { it.title.contains(query, ignoreCase = true) }.take(5)
        }
    }

    fun onBack() {
        onExpandedChange(false)
        onChangeQuery("")
    }

    SearchBar(
        modifier = modifier
            .fillMaxWidth(),
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = { onChangeQuery(it) },
                onSearch = {
                    onExpandedChange(false)
                },
                expanded = expanded,
                onExpandedChange = { onExpandedChange(it) },
                placeholder = {
                    Text("Поиск")
                },
                leadingIcon = {
                    if (expanded) {
                        IconButton(onClick = { onBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад"
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Поиск"
                        )
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onChangeQuery("") }) {
                            Icon(Icons.Default.Close, "Очистить")
                        }
                    }
                }
            )
        },
        expanded = expanded,
        onExpandedChange = { onExpandedChange(it) }
    ) {
        ListOfProducts(
            searchSuggestions,
            onNavigateToProduct
        )
    }
}

@Preview(showBackground = true, name = "Светлая тема")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Темная тема")
@Composable
private fun SearchBarPreview(){
    OzonPriceTrackingTheme {
        Surface {
            SearchBar(
                products = PreviewData.products,
                onNavigateToProduct = {},
                query = "Apple",
                onChangeQuery = {},
                expanded = true,
                onExpandedChange = {}
            )
        }
    }
}
