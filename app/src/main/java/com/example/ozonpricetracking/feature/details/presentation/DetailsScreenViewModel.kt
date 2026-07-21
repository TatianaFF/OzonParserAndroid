package com.example.ozonpricetracking.feature.details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozonpricetracking.domain.model.OzonProductWithPriceHistory
import com.example.ozonpricetracking.feature.details.domain.usecase.DeleteProductUseCase
import com.example.ozonpricetracking.feature.details.domain.usecase.GetProductWithPriceHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface ProductDetailsUiState {
    object Loading : ProductDetailsUiState
    data class Success(val data: OzonProductWithPriceHistory) : ProductDetailsUiState
    data class Error(val message: String) : ProductDetailsUiState
}

@HiltViewModel
class DetailsScreenViewModel @Inject constructor(
    private val getProductWithPriceHistory: GetProductWithPriceHistory,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailsUiState>(ProductDetailsUiState.Loading)
    val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    fun loadProduct(id: Long) {
        viewModelScope.launch {
            _uiState.value = ProductDetailsUiState.Loading

            getProductWithPriceHistory(id)
                .onSuccess { data ->
                    _uiState.value = ProductDetailsUiState.Success(data)
                }
                .onFailure { exception ->
                    _uiState.value = ProductDetailsUiState.Error("Ошибка получения товара")
                }
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            deleteProductUseCase.invoke(id)
        }
    }
}