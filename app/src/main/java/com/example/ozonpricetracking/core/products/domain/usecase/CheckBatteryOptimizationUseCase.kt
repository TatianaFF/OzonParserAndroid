package com.example.ozonpricetracking.core.products.domain.usecase

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import jakarta.inject.Inject
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext

class CheckBatteryOptimizationUseCase @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun isBatteryOptimizationIgnored(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    @SuppressLint("BatteryLife")
    fun getBatteryOptimizationIntent(): Intent {
        return Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            .setData("package:${context.packageName}".toUri())
    }

    fun getFallbackIntent(): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData("package:${context.packageName}".toUri())
    }
}