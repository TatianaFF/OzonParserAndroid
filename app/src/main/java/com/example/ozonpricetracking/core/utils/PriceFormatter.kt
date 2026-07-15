package com.example.ozonpricetracking.core.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object PriceFormatter {
    private val symbols = DecimalFormatSymbols(Locale.forLanguageTag("ru")).apply {
        groupingSeparator = ' '
    }
    private val formatter = DecimalFormat("#,###", symbols)

    fun format(price: Int): String {
        return formatter.format(price)
    }

    fun formatWithCurrency(price: Int): String {
        return "${format(price)} ₽"
    }
}
