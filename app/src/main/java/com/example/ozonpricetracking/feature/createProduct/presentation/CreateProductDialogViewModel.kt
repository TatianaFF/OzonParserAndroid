package com.example.ozonpricetracking.feature.createProduct.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.example.ozonpricetracking.feature.createProduct.data.worker.AddProductWorker
import com.example.ozonpricetracking.feature.createProduct.domain.usecase.AddProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AddProductUiState {
    object Idle : AddProductUiState
    object Loading : AddProductUiState
    object Success : AddProductUiState
    data class Error(val message: String) : AddProductUiState
}

@HiltViewModel
class CreateProductDialogViewModel @Inject constructor(
    private val addProductUseCase: AddProductUseCase
) : ViewModel()  {
    private val _uiState = MutableStateFlow<AddProductUiState>(AddProductUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun saveProduct(url: String, onSuccess: () -> Unit) {
        if (url.isBlank()) {
            _uiState.value = AddProductUiState.Error("Ссылка не может быть пустой")
            return
        }

        if (!url.contains("ozon.ru", ignoreCase = true)) {
            _uiState.value = AddProductUiState.Error("Поддерживаются только ссылки на Ozon")
            return
        }

        viewModelScope.launch {
            addProductUseCase(extractUrl(url)).collectLatest { workInfo ->
                if (workInfo == null) return@collectLatest

                when (workInfo.state) {
                    WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING -> {
                        _uiState.value = AddProductUiState.Loading
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        _uiState.value = AddProductUiState.Success
                        onSuccess()
                    }
                    WorkInfo.State.FAILED -> {
                        val error = workInfo.outputData.getString(AddProductWorker.KEY_ERROR)
                            ?: "Ошибка при добавлении товара"
                        _uiState.value = AddProductUiState.Error(error)
                    }
                    WorkInfo.State.CANCELLED -> {
                        _uiState.value = AddProductUiState.Error("Операция отменена")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun extractUrl(text: String): String {
        val regex = "https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=]+".toRegex()
        return regex.find(text)?.value ?: text
    }
}