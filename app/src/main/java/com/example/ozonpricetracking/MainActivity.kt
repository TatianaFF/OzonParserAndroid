package com.example.ozonpricetracking

import android.annotation.SuppressLint
import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.ozonpricetracking.core.products.domain.usecase.CheckBatteryOptimizationUseCase
import com.example.ozonpricetracking.core.theme.OzonPriceTrackingTheme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var checkBatteryUseCase: CheckBatteryOptimizationUseCase

    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        enableEdgeToEdge()

        setContent {
            OzonPriceTrackingTheme {
                Scaffold { innerPadding ->
                    AppNavigation(
                        sharedViewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }

//        requestBatteryOptimizationIfNeeded()

    }

    private fun requestBatteryOptimizationIfNeeded() {
        if (!checkBatteryUseCase.isBatteryOptimizationIgnored()) {
            try {
                startActivity(checkBatteryUseCase.getBatteryOptimizationIntent())
            } catch (e: Exception) {
                startActivity(checkBatteryUseCase.getFallbackIntent())
            }
        }
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            viewModel.handleIncomingUrl(text)
        }
    }
}