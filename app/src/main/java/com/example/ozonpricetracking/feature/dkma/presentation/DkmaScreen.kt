package com.example.ozonpricetracking.feature.dkma.presentation

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ozonpricetracking.feature.dkma.presentation.components.HtmlTextViewer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DkmaScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DkmaViewModel = viewModel()
) {
    val uiState by viewModel.dkmaState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDKMAData()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {  },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is DkmaUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is DkmaUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Не удалось загрузить данные.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.getDKMAData() }) {
                            Text("Повторить")
                        }
                    }
                }
                is DkmaUiState.Success -> {
                    HtmlTextViewer(
                        htmlContent = state.htmlContent,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
