package com.example.ozonpricetracking.feature.permissions.presentation

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ozonpricetracking.core.theme.OzonPriceTrackingTheme

@Composable
fun PermissionsScreen(
    viewModel: PermissionsScreenViewModel,
    onNavigateToDkma: () -> Unit,
    onPermissionsGranted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState

    // Следим за изменением статуса разрешений
    LaunchedEffect(uiState.allGranted) {
        if (uiState.allGranted) {
            onPermissionsGranted()
        }
    }

    // Обновляем статус при возвращении из настроек
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    PermissionsContent(
        uiState = uiState,
        onGrantPermission = {
            try {
                context.startActivity(viewModel.getBatteryIntent())
            } catch (e: Exception) {
                context.startActivity(viewModel.getFallbackIntent())
            }
        },
        onNavigateToDkma = onNavigateToDkma,
        onSkip = onSkip,
        modifier = modifier
    )
}

@Composable
fun PermissionsContent(
    uiState: PermissionsUiState,
    onGrantPermission: () -> Unit,
    onNavigateToDkma: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.BatteryAlert,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "⚡ Фоновая работа",
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Для автоматического обновления цен нужно разрешить фоновую работу",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Статус разрешения
        PermissionStatusItem(
            title = "Фоновая работа",
            description = "Обновление цен даже когда приложение закрыто",
            isGranted = uiState.allGranted,
            onAction = onGrantPermission
        )

        Spacer(modifier = Modifier.height(16.dp))

        VendorOptimizationItem (
            onNavigateToDkma = onNavigateToDkma
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(
            onClick = onSkip
        ) {
            Text(
                text = "Пропустить (цены не будут обновляться)",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PermissionStatusItem(
    title: String,
    description: String,
    isGranted: Boolean,
    onAction: () -> Unit,
    actionText: String = "Разрешить"
) {
    val isInstruction = actionText.contains("Инструкция", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isGranted -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                isInstruction -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
            }
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                if (isGranted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Разрешено",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            if (!isGranted) {
                Spacer(modifier = Modifier.height(12.dp))
                if (isInstruction) {
                    OutlinedButton(
                        onClick = onAction,
                        modifier = Modifier.align(Alignment.End),
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(actionText)
                    }
                } else {
                    Button(
                        onClick = onAction,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(actionText)
                    }
                }
            }
        }
    }
}

@Composable
fun VendorOptimizationItem(
    onNavigateToDkma: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val manufacturer = remember { 
        if (isPreview) "samsung" else Build.MANUFACTURER.lowercase() 
    }

    // Получаем данные для конкретного вендора
    val vendorData = getVendorData(manufacturer) ?: return

    PermissionStatusItem(
        title = vendorData.title,
        description = vendorData.description,
        isGranted = false, // Нельзя проверить программно
        onAction = onNavigateToDkma,
        actionText = vendorData.actionText
    )
}

data class VendorData(
    val title: String,
    val description: String,
    val actionText: String
)

fun getVendorData(manufacturer: String): VendorData? {
    return when (manufacturer) {
        "samsung" -> VendorData(
            title = "Samsung: Фоновая работа",
            description = "Добавьте приложение в список «Никогда не переводить в спящий режим», иначе Samsung заморозит его через 3 дня",
            actionText = "Инструкция для Samsung"
        )
        "xiaomi" -> VendorData(
            title = "Xiaomi: Автозапуск",
            description = "Разрешите автозапуск в настройках, иначе Xiaomi заблокирует фоновую работу",
            actionText = "Инструкция для Xiaomi"
        )
        "huawei" -> VendorData(
            title = "Huawei: Фоновая активность",
            description = "Включите ручное управление запуском, иначе Huawei остановит приложение в фоне",
            actionText = "Инструкция для Huawei"
        )
        "oneplus" -> VendorData(
            title = "OnePlus: Оптимизация",
            description = "Отключите оптимизацию батареи, иначе OnePlus ограничит фоновую работу",
            actionText = "Инструкция для OnePlus"
        )
        "oppo" -> VendorData(
            title = "Oppo: Автозапуск",
            description = "Разрешите автозапуск, иначе Oppo не даст приложению работать в фоне",
            actionText = "Инструкция для Oppo"
        )
        "vivo" -> VendorData(
            title = "Vivo: Фоновая активность",
            description = "Включите неограниченную фоновую активность, иначе Vivo остановит приложение",
            actionText = "Инструкция для Vivo"
        )
        else -> null // Google, Motorola и др. — не требуют доп. настроек
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionsContent_NotGranted_Preview() {
    OzonPriceTrackingTheme {
        PermissionsContent(
            uiState = PermissionsUiState(allGranted = false),
            onGrantPermission = {},
            onNavigateToDkma = {},
            onSkip = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionsContent_Granted_Preview() {
    OzonPriceTrackingTheme {
        PermissionsContent(
            uiState = PermissionsUiState(allGranted = true),
            onGrantPermission = {},
            onNavigateToDkma = {},
            onSkip = {}
        )
    }
}
