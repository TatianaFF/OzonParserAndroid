package com.example.ozonpricetracking.feature.permissions.presentation

import android.os.Build
import android.widget.Toast
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

    LaunchedEffect(uiState.allGranted) {
        if (uiState.allGranted) {
            onPermissionsGranted()
        }
    }

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
                context.startActivity(viewModel.getAppSettingsIntent())
            } catch (e: Exception) {
                Toast.makeText(context, "Пожалуйста, откройте настройки телефона вручную и найдите приложение в списке", Toast.LENGTH_LONG).show()
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

        PermissionStatusItem(
            title = "Фоновая работа",
            description = "Отключите все ограничения фоновой работы в настройках приложения",
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
    actionText: String = "Открыть"
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

//                if (isGranted) {
//                    Icon(
//                        imageVector = Icons.Default.CheckCircle,
//                        contentDescription = "Разрешено",
//                        tint = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
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

    val vendorData = getVendorData(manufacturer) ?: return

    PermissionStatusItem(
        title = vendorData.title,
        description = vendorData.description,
        isGranted = false, 
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
    return when (manufacturer.lowercase()) {
        "samsung" -> VendorData(
            title = "Samsung: Фоновая работа",
            description = "Отключите режим экономии и добавьте приложение в список «Никогда не переводить в спящий режим»",
            actionText = "Инструкция для Samsung"
        )
        "xiaomi", "redmi" -> VendorData(
            title = "Xiaomi/Redmi: Автозапуск",
            description = "Включите автозапуск и отключите оптимизацию батареи в настройках MIUI",
            actionText = "Инструкция для Xiaomi"
        )
        "huawei" -> VendorData(
            title = "Huawei: Фоновая активность",
            description = "Включите автозапуск, вторичный запуск и работу в фоне в настройках приложения",
            actionText = "Инструкция для Huawei"
        )
        "honor" -> VendorData(
            title = "Honor: Фоновая работа",
            description = "Отключите оптимизацию батареи и включите автозапуск в настройках",
            actionText = "Инструкция для Honor"
        )
        "oppo" -> VendorData(
            title = "Oppo: Автозапуск",
            description = "Включите автозапуск в Менеджере автозапуска и отключите фоновую заморозку",
            actionText = "Инструкция для Oppo"
        )
        "realme" -> VendorData(
            title = "Realme: Автозапуск",
            description = "Включите автозапуск и отключите быструю заморозку в настройках батареи",
            actionText = "Инструкция для Realme"
        )
        "vivo" -> VendorData(
            title = "Vivo: Фоновая активность",
            description = "Включите автозапуск и разрешите неограниченную фоновую активность",
            actionText = "Инструкция для Vivo"
        )
        "oneplus" -> VendorData(
            title = "OnePlus: Оптимизация",
            description = "Отключите оптимизацию батареи в настройках приложения",
            actionText = "Инструкция для OnePlus"
        )
        "lenovo" -> VendorData(
            title = "Lenovo: Энергосбережение",
            description = "Отключите режим энергосбережения и добавьте приложение в исключения",
            actionText = "Инструкция для Lenovo"
        )
        "meizu" -> VendorData(
            title = "Meizu: Защита приложений",
            description = "Добавьте приложение в список защищенных в настройках батареи",
            actionText = "Инструкция для Meizu"
        )
        "asus" -> VendorData(
            title = "Asus: Умная оптимизация",
            description = "Отключите умную оптимизацию и включите автозапуск",
            actionText = "Инструкция для Asus"
        )
        "motorola" -> VendorData(
            title = "Motorola: Оптимизация батареи",
            description = "Отключите оптимизацию батареи в настройках приложения",
            actionText = "Инструкция для Motorola"
        )
        "sony" -> VendorData(
            title = "Sony: Режим Stamina",
            description = "Отключите режим Stamina или добавьте приложение в исключения",
            actionText = "Инструкция для Sony"
        )
        "nokia" -> VendorData(
            title = "Nokia: Battery Protection",
            description = "Отключите Battery Protection и оптимизацию батареи",
            actionText = "Инструкция для Nokia"
        )
        "htc" -> VendorData(
            title = "HTC: Оптимизация батареи",
            description = "Отключите оптимизацию батареи через настройки приложения",
            actionText = "Инструкция для HTC"
        )
        "blackview" -> VendorData(
            title = "Blackview: Исключения",
            description = "Добавьте приложение в исключения оптимизации батареи",
            actionText = "Инструкция для Blackview"
        )
        "tecno" -> VendorData(
            title = "Tecno: Автозапуск",
            description = "Включите автозапуск и отключите HiOS оптимизацию",
            actionText = "Инструкция для Tecno"
        )
        "unihertz" -> VendorData(
            title = "Unihertz: Оптимизация",
            description = "Отключите оптимизацию батареи в настройках",
            actionText = "Инструкция для Unihertz"
        )
        else -> null
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