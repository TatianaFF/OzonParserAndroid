package com.example.ozonpricetracking.feature.details.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ozonpricetracking.domain.model.OzonPriceHistoryInfo
import com.example.ozonpricetracking.core.theme.OzonPriceTrackingTheme
import com.example.ozonpricetracking.core.utils.PriceFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun InteractiveLineChart(
    data: List<OzonPriceHistoryInfo>,
    modifier: Modifier = Modifier
) {
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val textMeasurer = rememberTextMeasurer()
    val primary = MaterialTheme.colorScheme.primary

    val maxPriceIndex = remember(data) { data.indexOfFirst { it.price == data.maxOf { item -> item.price } } }
    val minPriceIndex = remember(data) { data.indexOfFirst { it.price == data.minOf { item -> item.price } } }
    val currentPriceIndex = remember(data) { data.lastIndex }

    val monthLabelFormatter = remember { SimpleDateFormat("LLL", Locale.forLanguageTag("ru")) }

    val tooltipDateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ru")) }
    val tooltipTimeFormatter = remember { SimpleDateFormat("HH:mm", Locale.forLanguageTag("ru")) }

    val bottomPaddingPx = 60f 
    val topPaddingPx = 40f    

    var selectedIndex by remember { mutableStateOf(-1) }
    LaunchedEffect(data) { selectedIndex = -1 }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(12.dp))
            .pointerInput(data) {
                val pLeft = 16.dp.toPx()
                val pRight = 64.dp.toPx()

                fun updateSelectedIndex(x: Float) {
                    val usableWidth = size.width - pLeft - pRight
                    val stepX = usableWidth / (data.size - 1)

                    val correctedX = (x - pLeft).coerceIn(0f, usableWidth)
                    val index = (correctedX / stepX).coerceIn(0f, (data.size - 1).toFloat())
                    selectedIndex = index.roundToInt()
                }

                detectDragGestures(
                    onDragStart = { offset -> updateSelectedIndex(offset.x) },
                    onDragEnd = { selectedIndex = -1 },
                    onDragCancel = { selectedIndex = -1 },
                    onDrag = { change, _ -> updateSelectedIndex(change.position.x) }
                )
            }
            .pointerInput(data) {
                val pLeft = 16.dp.toPx()
                val pRight = 64.dp.toPx()

                detectTapGestures(
                    onPress = { offset ->
                        val usableWidth = size.width - pLeft - pRight
                        val stepX = usableWidth / (data.size - 1)

                        val correctedX = (offset.x - pLeft).coerceIn(0f, usableWidth)
                        selectedIndex = (correctedX / stepX).roundToInt().coerceIn(0, data.size - 1)
                        tryAwaitRelease()
                        selectedIndex = -1
                    }
                )
            }
    ){
        val paddingLeft = 16.dp.toPx()
        val paddingRight = 64.dp.toPx()
        val paddingTop = 24.dp.toPx()
        val paddingBottom = 48.dp.toPx()

        val width = size.width - paddingLeft - paddingRight
        val height = size.height - paddingTop - paddingBottom

        val stepX = if (data.size > 1) width / (data.size - 1) else width

        val maxPrice = (data.maxOfOrNull { it.price } ?: 0).toFloat()
        val minPrice = (data.minOfOrNull { it.price } ?: 0).toFloat()
        val priceRange = if (maxPrice == minPrice) 1f else (maxPrice - minPrice)

        val points = data.mapIndexed { index, item ->
            val x = paddingLeft + (index * stepX) 
            val y = paddingTop + height - ((item.price - minPrice) / priceRange * height) 
            Offset(x, y)
        }

        if (maxPriceIndex in points.indices && minPriceIndex in points.indices && currentPriceIndex in points.indices) {
            val maxPoint = points[maxPriceIndex]
            val minPoint = points[minPriceIndex]
            val currentPoint = points[currentPriceIndex]

            val badgeWidth = 55.dp.toPx()
            val badgeHeight = 18.dp.toPx()
            val badgeX = size.width - badgeWidth - 16f

            val minGap = 24.dp.toPx()

            var maxDataY = maxPoint.y
            var minDataY = minPoint.y
            var currentDataY = currentPoint.y

            if (Math.abs(currentDataY - maxDataY) < minGap) {
                currentDataY = maxDataY + minGap
            }
            if (Math.abs(minDataY - currentDataY) < minGap) {
                minDataY = currentDataY + minGap
            }
            if (Math.abs(minDataY - maxDataY) < minGap * 2) {
                minDataY = maxDataY + minGap * 2
            }

            maxDataY = maxDataY.coerceIn(paddingTop, size.height - paddingBottom)
            currentDataY = currentDataY.coerceIn(paddingTop + minGap, size.height - paddingBottom)
            minDataY = minDataY.coerceIn(paddingTop + minGap * 2, size.height - paddingBottom)

            drawLine(
                color = textColor.copy(alpha = 0.2f),
                start = Offset(paddingLeft, maxPoint.y),
                end = Offset(size.width - paddingRight, maxPoint.y),
                strokeWidth = 1.dp.toPx()
            )

            val maxText = PriceFormatter.formatWithCurrency(data[maxPriceIndex].price)
            val maxLayout = textMeasurer.measure(maxText, style = TextStyle(color = textColor))
            drawText(
                textLayoutResult = maxLayout,
                topLeft = Offset(
                    x = badgeX + (badgeWidth - maxLayout.size.width) / 2f,
                    y = maxDataY - maxLayout.size.height / 2f
                )
            )

            val minText = PriceFormatter.formatWithCurrency(data[minPriceIndex].price)
            val minLayout = textMeasurer.measure(minText, style = TextStyle(color = textColor))
            drawText(
                textLayoutResult = minLayout,
                topLeft = Offset(
                    x = badgeX + (badgeWidth - minLayout.size.width) / 2f,
                    y = minDataY - minLayout.size.height / 2f
                )
            )

            drawLine(
                color = primary.copy(alpha = 0.4f),
                start = Offset(paddingLeft, currentPoint.y),
                end = Offset(size.width - paddingRight, currentPoint.y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            val currentText = PriceFormatter.formatWithCurrency(data[currentPriceIndex].price)
            val currentLayout = textMeasurer.measure(currentText, style = TextStyle(color = primary))
            drawText(
                textLayoutResult = currentLayout,
                topLeft = Offset(
                    x = badgeX + (badgeWidth - currentLayout.size.width) / 2f,
                    y = currentDataY - currentLayout.size.height / 2f
                )
            )
        }

        val xAxisY = size.height - paddingBottom

        drawLine(
            color = Color.Gray,
            start = Offset(x = paddingLeft, y = xAxisY),
            end = Offset(x = size.width - paddingRight, y = xAxisY),
            strokeWidth = 1.dp.toPx()
        )

        drawLine(
            color = Color.Gray,
            start = Offset(x = paddingLeft, y = xAxisY),
            end = Offset(x = paddingLeft, y = xAxisY + 12f), 
            strokeWidth = 1.5.dp.toPx()
        )

        drawLine(
            color = Color.Gray,
            start = Offset(x = size.width - paddingRight, y = xAxisY),
            end = Offset(x = size.width - paddingRight, y = xAxisY + 12f), 
            strokeWidth = 1.5.dp.toPx()
        )

        for (index in 0 until data.size - 1) {
            val currentMonth = monthLabelFormatter.format(Date(data[index].createdAt))
            val nextMonth = monthLabelFormatter.format(Date(data[index + 1].createdAt))

            if (currentMonth != nextMonth) {
                val dividerX = (points[index].x + points[index + 1].x) / 2f

                drawLine(
                    color = Color.Gray,
                    start = Offset(x = dividerX, y = xAxisY),
                    end = Offset(x = dividerX, y = xAxisY + 12f),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }

        val groupedByMonth = data.indices.groupBy { index ->
            monthLabelFormatter.format(Date(data[index].createdAt))
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        groupedByMonth.forEach { (monthName, indices) ->
            val centerIndex = indices[indices.size / 2]
            val centerPointX = points[centerIndex].x

            val textLayoutResult1 = textMeasurer.measure(
                text = monthName,
                style = TextStyle(color = textColor, fontSize = 11.sp)
            )

            val textX = (centerPointX - textLayoutResult1.size.width / 2f)
                .coerceIn(paddingLeft, size.width - paddingRight - textLayoutResult1.size.width)

            drawText(
                textMeasurer = textMeasurer,
                text = monthName,
                style = TextStyle(color = textColor, fontSize = 11.sp),
                topLeft = Offset(
                    x = textX,
                    y = xAxisY + 16f
                )
            )
        }

        val path = Path().apply {
            points.forEachIndexed { index, offset ->
                if (index == 0) moveTo(offset.x, offset.y) else lineTo(offset.x, offset.y)
            }
        }
        drawPath(
            path = path,
            color = primary,
            style = Stroke(width = 3.dp.toPx())
        )

        if (selectedIndex in data.indices) {
            val selectedPoint = points[selectedIndex]
            val selectedItem = data[selectedIndex]

            drawLine(
                color = Color.LightGray,
                start = Offset(selectedPoint.x, topPaddingPx),
                end = Offset(selectedPoint.x, height - bottomPaddingPx),
                strokeWidth = 1.dp.toPx()
            )

            drawCircle(
                color = Color(0xFF3F51B5),
                radius = 6.dp.toPx(),
                center = selectedPoint
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = selectedPoint
            )

            val dateStr = tooltipDateFormatter.format(Date(selectedItem.createdAt))
            val timeStr = tooltipTimeFormatter.format(Date(selectedItem.createdAt))
            val priceStr = PriceFormatter.formatWithCurrency(selectedItem.price)

            val tooltipText = "$dateStr\n$timeStr\n$priceStr"

            val textLayoutResult = textMeasurer.measure(text = tooltipText,style = TextStyle(
                color = Color.White,
                fontSize = 12.sp
            )
            )

            val tooltipWidth = textLayoutResult.size.width + 24f
            val tooltipHeight = textLayoutResult.size.height + 16f

            val tooltipX = when {
                selectedPoint.x + tooltipWidth / 2 > width -> width - tooltipWidth - 10f
                selectedPoint.x - tooltipWidth / 2 < 0 -> 10f
                else-> selectedPoint.x - tooltipWidth / 2
            }

            val tooltipY = (selectedPoint.y - tooltipHeight - 20f).coerceAtLeast(10f)

            drawRoundRect(
                color = Color(0xCC1A1A1A), 
                topLeft = Offset(tooltipX, tooltipY),
                size = Size(tooltipWidth, tooltipHeight),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )

            drawText(
                textMeasurer = textMeasurer,
                text = tooltipText,
                style = TextStyle(color = Color.White, fontSize = 12.sp),
                topLeft = Offset(tooltipX + 12f, tooltipY + 8f)
            )
        }
    }
}

@Preview(showBackground = true, name = "Светлая тема")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Темная тема")
@Composable
fun InteractiveLineChartPreview() {
    val mockPrices = listOf(
        OzonPriceHistoryInfo(0, 1200, 1767225600000L + 3600000 * 10 + 60000 * 15),   
        OzonPriceHistoryInfo(0, 1150, 1768003200000L + 3600000 * 14 + 60000 * 30),   
        OzonPriceHistoryInfo(0, 1300, 1768867200000L + 3600000 * 18 + 60000 * 45),   
        OzonPriceHistoryInfo(0, 1250, 1769904000000L + 3600000 * 9 + 60000 * 20),    
        OzonPriceHistoryInfo(0, 1400, 1770681600000L + 3600000 * 12 + 60000 * 0),    
        OzonPriceHistoryInfo(0, 1350, 1771545600000L + 3600000 * 21 + 60000 * 10),   
        OzonPriceHistoryInfo(0, 1350, 1772323200000L + 3600000 * 8 + 60000 * 5),     
        OzonPriceHistoryInfo(0, 1100, 1773100800000L + 3600000 * 15 + 60000 * 40),   
        OzonPriceHistoryInfo(0, 1050, 1773964800000L + 3600000 * 23 + 60000 * 55),   
        OzonPriceHistoryInfo(0, 1000, 1775001600000L + 3600000 * 7 + 60000 * 30),    
        OzonPriceHistoryInfo(0, 950,  1775779200000L + 3600000 * 11 + 60000 * 15),   
        OzonPriceHistoryInfo(0, 900,  1776643200000L + 3600000 * 16 + 60000 * 50),   
        OzonPriceHistoryInfo(0, 1000, 1777593600000L + 3600000 * 10 + 60000 * 0),    
        OzonPriceHistoryInfo(0, 1100, 1778371200000L + 3600000 * 13 + 60000 * 25),   
        OzonPriceHistoryInfo(0, 1250, 1779235200000L + 3600000 * 19 + 60000 * 40),   
        OzonPriceHistoryInfo(0, 1350, 1780272000000L + 3600000 * 6 + 60000 * 10),    
        OzonPriceHistoryInfo(0, 1500, 1781049600000L + 3600000 * 14 + 60000 * 50),   
        OzonPriceHistoryInfo(0, 1450, 1781913600000L + 3600000 * 22 + 60000 * 30)    
    )

    OzonPriceTrackingTheme {
        InteractiveLineChart(
            data = mockPrices
        )
    }
}