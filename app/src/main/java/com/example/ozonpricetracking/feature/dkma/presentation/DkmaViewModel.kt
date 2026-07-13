package com.example.ozonpricetracking.feature.dkma.presentation

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.net.URL
import java.util.Locale
import javax.inject.Inject

sealed class DkmaUiState {
    object Loading : DkmaUiState()
    data class Success(val htmlContent: String) : DkmaUiState()
    object Error : DkmaUiState()
}

@HiltViewModel
class DkmaViewModel @Inject constructor() : ViewModel() {
    private val _dkmaState = MutableStateFlow<DkmaUiState>(DkmaUiState.Loading)
    val dkmaState: StateFlow<DkmaUiState> = _dkmaState

    fun getDKMAData() {
        viewModelScope.launch(Dispatchers.IO) {
            _dkmaState.value = DkmaUiState.Loading
            val manufacturer = Build.MANUFACTURER.lowercase(Locale.ROOT).replace(" ", "-")
            var result = downloadData(manufacturer)

            if (result == null && manufacturer != "unspecified") {
                result = downloadData("unspecified")
            }

            withContext(Dispatchers.Main) {
                if (result != null) {
                    _dkmaState.value = DkmaUiState.Success(result)
                } else {
                    _dkmaState.value = DkmaUiState.Error
                }
            }
        }
    }

    private fun downloadData(manufacturer: String): String? {
        return try {
            val url = URL("https://dontkillmyapp.com/api/v2/$manufacturer.json")
            val json = JSONTokener(url.readText()).nextValue() as JSONObject?
            json?.getString("user_solution")?.replace(Regex("\\[[Yy]our app]"), "")
        } catch (e: Exception) {
            null
        }
    }
}