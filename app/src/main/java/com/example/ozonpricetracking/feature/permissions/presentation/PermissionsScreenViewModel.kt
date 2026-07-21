package com.example.ozonpricetracking.feature.permissions.presentation

import android.content.Intent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.ViewModel
import com.example.ozonpricetracking.feature.permissions.domain.usecase.CheckBatteryOptimizationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionsScreenViewModel @Inject constructor(
    private val checkBatteryUseCase: CheckBatteryOptimizationUseCase
) : ViewModel()  {

    private val _uiState = mutableStateOf(
        PermissionsUiState(allGranted = checkBatteryUseCase.isBatteryOptimizationIgnored())
    )
    val uiState: State<PermissionsUiState> = _uiState

    fun checkPermissions() {
        val granted = checkBatteryUseCase.isBatteryOptimizationIgnored()
        _uiState.value = _uiState.value.copy(
            allGranted = granted
        )
    }

    fun getBatteryIntent(): Intent {
        return checkBatteryUseCase.getBatteryOptimizationIntent()
    }

    fun getFallbackIntent(): Intent {
        return checkBatteryUseCase.getFallbackIntent()
    }
}

data class PermissionsUiState(
    val allGranted: Boolean = false
)