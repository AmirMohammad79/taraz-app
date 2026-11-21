package com.example.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

enum class CurrencyUnit(val labelFa: String, val multiplierFromToman: Long) {
    TOMAN("تومان", 1L),
    RIAL("ریال", 10L),
}

object PersianFormatUtils {

    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun toPersianDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            when {
                ch in '0'..'9' -> sb.append(persianDigits[ch - '0'])
                ch in '٠'..'٩' -> sb.append(persianDigits[ch - '٠'])
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toAsciiDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            when {
                ch in '۰'..'۹' -> sb.append(('0' + (ch - '۰')))
                ch in '٠'..'٩' -> sb.append(('0' + (ch - '٠')))
                ch in '0'..'9' -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun formatNumber(value: Long, usePersianDigits: Boolean = true): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = '،'
        }
        val formatter = DecimalFormat("#,###", symbols)
        val formatted = formatter.format(value)
        return if (usePersianDigits) toPersianDigits(formatted) else formatted
    }

    fun formatCurrency(
        amountToman: Long,
        unit: CurrencyUnit = CurrencyUnit.TOMAN,
        usePersianDigits: Boolean = true,
        includeSign: Boolean = false,
        includeUnit: Boolean = false,
    ): String {
        val scaled = amountToman * unit.multiplierFromToman
        val absFormatted = formatNumber(abs(scaled), usePersianDigits)
        val sign = when {
            scaled < 0 -> "-"
            includeSign && scaled > 0 -> "+"
            else -> ""
        }
        val core = "$sign$absFormatted"
        return if (includeUnit) "$core ${unit.labelFa}" else core
    }

    fun formatPercent(value: Double, includePlus: Boolean = false, usePersianDigits: Boolean = true): String {
        val rounded = String.format(Locale.US, "%.1f", abs(value)).removeSuffix(".0")
        val prefix = when {
            value < 0 -> "-"
            includePlus && value > 0 -> "+"
            else -> ""
        }
        val raw = "$prefix$rounded٪"
        return if (usePersianDigits) toPersianDigits(raw) else raw
    }

    fun formatInputWithCommas(rawDigits: String, usePersianDigits: Boolean = true): String {
        val clean = toAsciiDigits(rawDigits).take(14)
        if (clean.isEmpty()) return ""
        val parsed = clean.toLongOrNull() ?: return ""
        return formatNumber(parsed, usePersianDigits)
    }
}
