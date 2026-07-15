package com.example.ozonpricetracking.feature.details.presentation

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ozonpricetracking.core.products.domain.model.OzonProductWithPriceHistory
import com.example.ozonpricetracking.core.theme.OzonPriceTrackingTheme
import com.example.ozonpricetracking.core.utils.PreviewData
import com.example.ozonpricetracking.core.utils.PriceFormatter
import com.example.ozonpricetracking.feature.details.presentation.components.DeleteConfirmationDialog
import com.example.ozonpricetracking.feature.details.presentation.components.DetailsErrorContent
import com.example.ozonpricetracking.feature.details.presentation.components.EmptyHistoryPlaceholder
import com.example.ozonpricetracking.feature.details.presentation.components.InteractiveLineChart
import com.example.ozonpricetracking.feature.details.presentation.components.PeriodDropdown
import com.example.ozonpricetracking.feature.details.presentation.components.ProductImageHeader
import java.util.Calendar

enum class Period(val label: String, val monthsCount: Int) {
    ONE_MONTH("1 месяц", 1),
    THREE_MONTHS("3 месяца", 3),
    SIX_MONTHS("6 месяцев", 6)
}

@Composable
fun DetailsScreen(
    id: Long,
    onBack: () -> Unit,
    onNavigateToDkma: () -> Unit,
    viewModel: DetailsScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(id) {
        viewModel.loadProduct(id)
    }

    DetailsScreenContent(
        uiState = uiState,
        onBack = onBack,
        onNavigateToDkma = onNavigateToDkma,
        onRetry = { viewModel.loadProduct(id) },
        onDeleteProduct = { viewModel.deleteProduct(id) }
    )
}

@Composable
fun DetailsScreenContent(
    uiState: ProductDetailsUiState,
    onBack: () -> Unit,
    onNavigateToDkma: () -> Unit,
    onRetry: () -> Unit,
    onDeleteProduct: () -> Unit
) {
    when (uiState) {
        is ProductDetailsUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ProductDetailsUiState.Success -> {
            ProductDetailsContent(
                uiState.data,
                onBack = onBack,
                onNavigateToDkma = onNavigateToDkma,
                onDeleteProduct = onDeleteProduct
            )
        }
        is ProductDetailsUiState.Error -> {
            DetailsErrorContent(
                message = uiState.message,
                onRetry = onRetry
            )
        }
    }
}

@Composable
fun ProductDetailsContent(
    p: OzonProductWithPriceHistory,
    onBack: () -> Unit,
    onNavigateToDkma: () -> Unit,
    onDeleteProduct: () -> Unit
){
    val context = LocalContext.current
    var selectedPeriod by remember { mutableStateOf(Period.ONE_MONTH) }
    val listState = rememberLazyListState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val filteredData = remember(selectedPeriod, p.history) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -selectedPeriod.monthsCount)
        val cutoffTime = calendar.timeInMillis

        p.history
            .filter { it.createdAt >= cutoffTime }
            .sortedBy { it.createdAt }
    }

    val currentPrice = p.history.lastOrNull()?.price ?: 0

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ProductImageHeader(
                imageUrl = p.product.image,
                title = p.product.title,
                onBackClick = onBack,
                onDeleteClick = { showDeleteDialog = true },
                onOpenLinkClick = { context.openLink(p.product.url) }
            )
        }

        item {
            Text(
                text = p.product.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 28.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.wrapContentSize()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = PriceFormatter.formatWithCurrency(currentPrice),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "История цен",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                PeriodDropdown(
                    selectedPeriod = selectedPeriod,
                    onPeriodSelected = { selectedPeriod = it }
                )
            }
        }

        if(filteredData.size > 1)
            item {
                InteractiveLineChart(data = filteredData)
            }
        else
            item {
                EmptyHistoryPlaceholder(onNavigateToInstructions = onNavigateToDkma)
            }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteDialog = false
                onDeleteProduct()
                onBack()
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
}

fun Context.openLink(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}

@Preview(showBackground = true, name = "Светлая тема")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Темная тема")
@Composable
private fun DetailsScreenPreview() {
    OzonPriceTrackingTheme {
        Surface {
            DetailsScreenContent(
                uiState = ProductDetailsUiState.Success(PreviewData.productWithHistory),
                onBack = {},
                onNavigateToDkma = {},
                onRetry = {},
                onDeleteProduct = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Светлая тема")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Темная тема")
@Composable
private fun DetailsScreenErrorPreview() {
    OzonPriceTrackingTheme {
        Surface {
            DetailsScreenContent(
                uiState = ProductDetailsUiState.Error("Не удалось загрузить данные о товаре. Проверьте интернет-соединение."),
                onBack = {},
                onNavigateToDkma = {},
                onRetry = {},
                onDeleteProduct = {}
            )
        }
    }
}
