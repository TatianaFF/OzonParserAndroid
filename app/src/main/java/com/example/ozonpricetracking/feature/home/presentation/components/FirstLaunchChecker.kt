package com.example.ozonpricetracking.feature.home.presentation.components

import android.content.Context
import android.os.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun FirstLaunchChecker(onNavigateToDkma: () -> Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // Проверяем флаг при первом запуске
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("is_first_launch_restrictions", true)

        if (isFirstLaunch) {
            showDialog = true
            // Сразу помечаем, что проверили, чтобы не спамить при поворотах экрана
            prefs.edit().putBoolean("is_first_launch_restrictions", false).apply()
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Работа в фоне") },
            text = {
                Text("Ваш телефон (${Build.MANUFACTURER}) может закрывать приложение в фоне. Пожалуйста, ознакомьтесь с инструкцией, чтобы приложение работало стабильно.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onNavigateToDkma() // Переходим на экран DkmaScreen
                }) {
                    Text("Инструкция")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Позже")
                }
            }
        )
    }
}
