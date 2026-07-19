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
import com.example.ozonpricetracking.core.products.domain.model.OzonPriceHistoryInfo
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

    // Находим индексы точек с максимальной и минимальной ценой
    val maxPriceIndex = remember(data) { data.indexOfFirst { it.price == data.maxOf { item -> item.price } } }
    val minPriceIndex = remember(data) { data.indexOfFirst { it.price == data.minOf { item -> item.price } } }
    val currentPriceIndex = remember(data) { data.lastIndex }

    // Форматтеры для дат
    val monthLabelFormatter = remember { SimpleDateFormat("LLL", Locale.forLanguageTag("ru")) }

    // Для тултипа оставляем старый формат даты цифрами
    val tooltipDateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ru")) }
    val tooltipTimeFormatter = remember { SimpleDateFormat("HH:mm", Locale.forLanguageTag("ru")) }

    // Разметка отступов внутри Canvas
    val bottomPaddingPx = 60f // Место под подписи месяцев внизу
    val topPaddingPx = 40f    // Отступ сверху, чтобы график не прилипал к краю

    // Индекс точки, на которую сейчас наведен палец (-1, если ничего не выбрано)
    var selectedIndex by remember { mutableStateOf(-1) }
    // Сбрасываем выбранную точку при изменении набора данных
    LaunchedEffect(data) { selectedIndex = -1 }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(12.dp))
            .pointerInput(data) {
                // Переводим отступы в пиксели через контекст pointerInput
                val pLeft = 16.dp.toPx()
                val pRight = 64.dp.toPx()

                // Функция определения ближайшей точки по координате x
                fun updateSelectedIndex(x: Float) {
                    val usableWidth = size.width - pLeft - pRight
                    val stepX = usableWidth / (data.size - 1)

                    // Вычитаем левый отступ, чтобы точка 0 начиналась там же, где и линия графика
                    val correctedX = (x - pLeft).coerceIn(0f, usableWidth)
                    val index = (correctedX / stepX).coerceIn(0f, (data.size - 1).toFloat())
                    selectedIndex = index.roundToInt()
                }

                // Отслеживание перетаскивания (ведения пальцем)
                detectDragGestures(
                    onDragStart = { offset -> updateSelectedIndex(offset.x) },
                    onDragEnd = { selectedIndex = -1 },
                    onDragCancel = { selectedIndex = -1 },
                    onDrag = { change, _ -> updateSelectedIndex(change.position.x) }
                )
            }
            // Также обрабатываем простое одиночное нажатие/удержание
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
        // Задаем желаемые отступы в пикселях (конвертируем dp в px)
        val paddingLeft = 16.dp.toPx()
        val paddingRight = 64.dp.toPx()
        val paddingTop = 24.dp.toPx()
        val paddingBottom = 48.dp.toPx()

        // Вычисляем ПОЛЕЗНУЮ (рабочую) ширину и высоту для графика
        val width = size.width - paddingLeft - paddingRight
        val height = size.height - paddingTop - paddingBottom

        // Защита от деления на ноль, если в списке мало точек
        val stepX = if (data.size > 1) width / (data.size - 1) else width

        val maxPrice = (data.maxOfOrNull { it.price } ?: 0).toFloat()
        val minPrice = (data.minOfOrNull { it.price } ?: 0).toFloat()
        val priceRange = if (maxPrice == minPrice) 1f else (maxPrice - minPrice)

        // Вычисляем координаты точек с учетом левого и верхнего отступов
        val points = data.mapIndexed { index, item ->
            val x = paddingLeft + (index * stepX) // Сдвигаем вправо на paddingLeft
            val y = paddingTop + height - ((item.price - minPrice) / priceRange * height) // Сдвигаем вниз на paddingTop
            Offset(x, y)
        }

        // Проверяем наличие всех индексов в массиве точек
        if (maxPriceIndex in points.indices && minPriceIndex in points.indices && currentPriceIndex in points.indices) {
            val maxPoint = points[maxPriceIndex]
            val minPoint = points[minPriceIndex]
            val currentPoint = points[currentPriceIndex]

            val badgeWidth = 55.dp.toPx()
            val badgeHeight = 18.dp.toPx()
            val badgeX = size.width - badgeWidth - 16f

            // Минимальный зазор между центрами текстов
            val minGap = 24.dp.toPx()

            // Берем исходные координаты Y
            var maxDataY = maxPoint.y
            var minDataY = minPoint.y
            var currentDataY = currentPoint.y

            // Сортируем координаты по возрастанию (в Android верх — это меньший Y), чтобы разводить их строго по порядку
            // 1. Максимум и Текущая
            if (Math.abs(currentDataY - maxDataY) < minGap) {
                currentDataY = maxDataY + minGap
            }
            // 2. Текущая и Минимум
            if (Math.abs(minDataY - currentDataY) < minGap) {
                minDataY = currentDataY + minGap
            }
            // 3. Проверка на случай, если Минимум догнал Максимум при резких скачках
            if (Math.abs(minDataY - maxDataY) < minGap * 2) {
                minDataY = maxDataY + minGap * 2
            }

            // Защита от вылета за границы Canvas сверху и снизу
            maxDataY = maxDataY.coerceIn(paddingTop, size.height - paddingBottom)
            currentDataY = currentDataY.coerceIn(paddingTop + minGap, size.height - paddingBottom)
            minDataY = minDataY.coerceIn(paddingTop + minGap * 2, size.height - paddingBottom)

            // --- ЛИНИЯ МАКСИМАЛЬНОЙ ЦЕНЫ ---
            drawLine(
                color = textColor.copy(alpha = 0.2f),
                start = Offset(paddingLeft, maxPoint.y),
                end = Offset(size.width - paddingRight, maxPoint.y),
                strokeWidth = 1.dp.toPx()
            )

            // Текст максимума (Используем скорректированный maxDataY)
            val maxText = PriceFormatter.formatWithCurrency(data[maxPriceIndex].price)
            val maxLayout = textMeasurer.measure(maxText, style = TextStyle(color = textColor))
            drawText(
                textLayoutResult = maxLayout,
                topLeft = Offset(
                    x = badgeX + (badgeWidth - maxLayout.size.width) / 2f,
                    y = maxDataY - maxLayout.size.height / 2f
                )
            )

            // Текст минимума (Используем скорректированный minDataY)
            val minText = PriceFormatter.formatWithCurrency(data[minPriceIndex].price)
            val minLayout = textMeasurer.measure(minText, style = TextStyle(color = textColor))
            drawText(
                textLayoutResult = minLayout,
                topLeft = Offset(
                    x = badgeX + (badgeWidth - minLayout.size.width) / 2f,
                    y = minDataY - minLayout.size.height / 2f
                )
            )

            // --- ЛИНИЯ ТЕКУЩЕЙ ЦЕНЫ ---
            drawLine(
                color = primary.copy(alpha = 0.4f),
                start = Offset(paddingLeft, currentPoint.y),
                end = Offset(size.width - paddingRight, currentPoint.y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            // Текст текущей цены (Используем скорректированный currentDataY)
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

        // Вычисляем точную координату Y для горизонтальной оси X
        // Она должна проходить ровно по нижней границе рабочей зоны графика
        val xAxisY = size.height - paddingBottom

        // Рисуем сплошную горизонтальную линию оси X (в пределах графика)
        drawLine(
            color = Color.Gray,
            start = Offset(x = paddingLeft, y = xAxisY),
            end = Offset(x = size.width - paddingRight, y = xAxisY),
            strokeWidth = 1.dp.toPx()
        )

        // ЧЁРТОЧКА НА ЛЕВОМ КРАЮ ОСИ X ---
        drawLine(
            color = Color.Gray,
            start = Offset(x = paddingLeft, y = xAxisY),
            end = Offset(x = paddingLeft, y = xAxisY + 12f), // уходит вниз на 12 пикселей
            strokeWidth = 1.5.dp.toPx()
        )

        // ЧЁРТОЧКА НА ПРАВОМ КРАЮ ОСИ X ---
        drawLine(
            color = Color.Gray,
            start = Offset(x = size.width - paddingRight, y = xAxisY),
            end = Offset(x = size.width - paddingRight, y = xAxisY + 12f), // уходит вниз на 12 пикселей
            strokeWidth = 1.5.dp.toPx()
        )

        // ОТРИСОВКА ЧЕРТОЧЕК (РАЗДЕЛИТЕЛЕЙ) МЕЖДУ МЕСЯЦАМИ
        for (index in 0 until data.size - 1) {
            val currentMonth = monthLabelFormatter.format(Date(data[index].createdAt))
            val nextMonth = monthLabelFormatter.format(Date(data[index + 1].createdAt))

            // Если у следующей точки месяц изменился — рисуем чёрточку
            if (currentMonth != nextMonth) {
                // Координата X берется как среднее между точками из обновленного массива points
                val dividerX = (points[index].x + points[index + 1].x) / 2f

                // Рисуем вертикальную чёрточку, идущую от оси X вниз на 12 пикселей
                drawLine(
                    color = Color.Gray,
                    start = Offset(x = dividerX, y = xAxisY),
                    end = Offset(x = dividerX, y = xAxisY + 12f),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }

        // ОТРИСОВКА НАЗВАНИЙ МЕСЯЦЕВ ПО ЦЕНТРУ ГРУПП
        val groupedByMonth = data.indices.groupBy { index ->
            monthLabelFormatter.format(Date(data[index].createdAt))
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        groupedByMonth.forEach { (monthName, indices) ->
            // Находим центр месяца среди точек
            val centerIndex = indices[indices.size / 2]
            val centerPointX = points[centerIndex].x

            val textLayoutResult1 = textMeasurer.measure(
                text = monthName,
                style = TextStyle(color = textColor, fontSize = 11.sp)
            )

            // Центрируем текст по оси X с учетом боковых отступов канваса
            val textX = (centerPointX - textLayoutResult1.size.width / 2f)
                .coerceIn(paddingLeft, size.width - paddingRight - textLayoutResult1.size.width)

            // Рисуем текст чуть ниже черточек (на 16 пикселей ниже оси X)
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

        // --- ОТРИСОВКА ОСНОВНОЙ ЛИНИИ ГРАФИКА ---
        // Рисуется строго после линий сетки, чтобы линия тренда была поверх них
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

        // Отрисовка Tooltip (всплывающего окна), если точка выбрана
        if (selectedIndex in data.indices) {
            val selectedPoint = points[selectedIndex]
            val selectedItem = data[selectedIndex]

            // Вертикальная вспомогательная линия (гайдлайн)
            drawLine(
                color = Color.LightGray,
                start = Offset(selectedPoint.x, topPaddingPx),
                end = Offset(selectedPoint.x, height - bottomPaddingPx),
                strokeWidth = 1.dp.toPx()
            )

            // Активная точка на графике
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

            // Текст внутри подсказки
            val dateStr = tooltipDateFormatter.format(Date(selectedItem.createdAt))
            val timeStr = tooltipTimeFormatter.format(Date(selectedItem.createdAt))
            val priceStr = PriceFormatter.formatWithCurrency(selectedItem.price)

            val tooltipText = "$dateStr\n$timeStr\n$priceStr"

            val textLayoutResult = textMeasurer.measure(text = tooltipText,style = TextStyle(
                color = Color.White,
                fontSize = 12.sp
            )
            )

            // Размеры и позиционирование плашки тултипа
            val tooltipWidth = textLayoutResult.size.width + 24f
            val tooltipHeight = textLayoutResult.size.height + 16f

            // Сдвигаем тултип влево или вправо, чтобы он не уходил за границы экрана
            val tooltipX = when {
                selectedPoint.x + tooltipWidth / 2 > width -> width - tooltipWidth - 10f
                selectedPoint.x - tooltipWidth / 2 < 0 -> 10f
                else-> selectedPoint.x - tooltipWidth / 2
            }

            // Размещаем чуть выше выбранной точки
            val tooltipY = (selectedPoint.y - tooltipHeight - 20f).coerceAtLeast(10f)

            // Рисуем подложку прямоугольника с закругленными краями
            drawRoundRect(
                color = Color(0xCC1A1A1A), // Полупрозрачный черный
                topLeft = Offset(tooltipX, tooltipY),
                size = Size(tooltipWidth, tooltipHeight),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )

            // Рисуем текст в тултипе
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
    // Генерируем тестовый список истории цен для отображения в дизайнере [2]
    val mockPrices = listOf(
        // Январь 2026
        OzonPriceHistoryInfo(0, 1200, 1767225600000L + 3600000 * 10 + 60000 * 15),   // 01.01.2026 10:15
        OzonPriceHistoryInfo(0, 1150, 1768003200000L + 3600000 * 14 + 60000 * 30),   // 10.01.2026 14:30
        OzonPriceHistoryInfo(0, 1300, 1768867200000L + 3600000 * 18 + 60000 * 45),   // 20.01.2026 18:45
        // Февраль 2026
        OzonPriceHistoryInfo(0, 1250, 1769904000000L + 3600000 * 9 + 60000 * 20),    // 01.02.2026 09:20
        OzonPriceHistoryInfo(0, 1400, 1770681600000L + 3600000 * 12 + 60000 * 0),    // 10.02.2026 12:00
        OzonPriceHistoryInfo(0, 1350, 1771545600000L + 3600000 * 21 + 60000 * 10),   // 20.02.2026 21:10
        // Март 2026
        OzonPriceHistoryInfo(0, 1350, 1772323200000L + 3600000 * 8 + 60000 * 5),     // 01.03.2026 08:05
        OzonPriceHistoryInfo(0, 1100, 1773100800000L + 3600000 * 15 + 60000 * 40),   // 10.03.2026 15:40
        OzonPriceHistoryInfo(0, 1050, 1773964800000L + 3600000 * 23 + 60000 * 55),   // 20.03.2026 23:55
        // Апрель 2026
        OzonPriceHistoryInfo(0, 1000, 1775001600000L + 3600000 * 7 + 60000 * 30),    // 01.04.2026 07:30
        OzonPriceHistoryInfo(0, 950,  1775779200000L + 3600000 * 11 + 60000 * 15),   // 10.04.2026 11:15
        OzonPriceHistoryInfo(0, 900,  1776643200000L + 3600000 * 16 + 60000 * 50),   // 20.04.2026 16:50
        // Май 2026
        OzonPriceHistoryInfo(0, 1000, 1777593600000L + 3600000 * 10 + 60000 * 0),    // 01.05.2026 10:00
        OzonPriceHistoryInfo(0, 1100, 1778371200000L + 3600000 * 13 + 60000 * 25),   // 10.05.2026 13:25
        OzonPriceHistoryInfo(0, 1250, 1779235200000L + 3600000 * 19 + 60000 * 40),   // 20.05.2026 19:40
        // Июнь 2026
        OzonPriceHistoryInfo(0, 1350, 1780272000000L + 3600000 * 6 + 60000 * 10),    // 01.06.2026 06:10
        OzonPriceHistoryInfo(0, 1500, 1781049600000L + 3600000 * 14 + 60000 * 50),   // 10.06.2026 14:50
        OzonPriceHistoryInfo(0, 1450, 1781913600000L + 3600000 * 22 + 60000 * 30)    // 20.06.2026 22:30
    )

    // Оборачиваем в тему вашего приложения, чтобы цвета кнопок и фона подставились сами [2]
    OzonPriceTrackingTheme {
        InteractiveLineChart(
            data = mockPrices
        )
    }
}
