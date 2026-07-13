package com.example.ozonpricetracking.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ozonpricetracking.feature.home.presentation.components.AddButton
import com.example.ozonpricetracking.feature.home.presentation.components.ListOfProducts
import com.example.ozonpricetracking.feature.home.presentation.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProduct: (id: Long) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDkma: () -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable  { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val products by viewModel.productState.collectAsStateWithLifecycle()

    LaunchedEffect(expanded) {
        if (!expanded) {
            query = ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        if(!expanded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AddButton(onNavigateToCreate)
                IconButton(onClick = onNavigateToDkma) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                        contentDescription = "Help"
                    )
                }
            }
        }

        SearchBar(
            products,
            onNavigateToProduct,
            query,
            onChangeQuery = { query = it },
            expanded,
            onExpandedChange = { expanded = it },
        )

        if(!expanded)
            ListOfProducts(
                products,
                onNavigateToProduct
            )
    }
}
