package com.example.util

import java.util.Calendar

data class ShamsiDate(
    val year: Int,
    val month: Int, // 1..12
    val day: Int,   // 1..31
    val dayOfWeekName: String,
) {
    val monthName: String
        get() = MONTH_NAMES.getOrElse(month - 1) { "" }

    fun toFormattedSlash(usePersianDigits: Boolean = true): String {
        val m = month.toString().padStart(2, '0')
        val d = day.toString().padStart(2, '0')
        val raw = "$year/$m/$d"
        return if (usePersianDigits) PersianFormatUtils.toPersianDigits(raw) else raw
    }

    fun toHeaderDisplay(usePersianDigits: Boolean = true): String {
        val raw = "امروز، $day $monthName $year"
        return if (usePersianDigits) PersianFormatUtils.toPersianDigits(raw) else raw
    }

    fun toShortDayMonth(usePersianDigits: Boolean = true): String {
        val raw = "$day $monthName"
        return if (usePersianDigits) PersianFormatUtils.toPersianDigits(raw) else raw
    }

    companion object {
        val MONTH_NAMES = listOf(
            "فروردین", "اردیبهشت", "خرداد",
            "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر",
            "دی", "بهمن", "اسفند"
        )
    }
}

object ShamsiDateUtils {

    fun nowShamsi(): ShamsiDate {
        return fromCalendar(Calendar.getInstance())
    }

    fun fromTimestamp(epochMs: Long): ShamsiDate {
        val cal = Calendar.getInstance().apply { timeInMillis = epochMs }
        return fromCalendar(cal)
    }

    fun fromCalendar(calendar: Calendar): ShamsiDate {
        val gy = calendar.get(Calendar.YEAR)
        val gm = calendar.get(Calendar.MONTH) + 1
        val gd = calendar.get(Calendar.DAY_OF_MONTH)
        val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
        val dowName = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> "شنبه"
            Calendar.SUNDAY -> "یک‌شنبه"
            Calendar.MONDAY -> "دوشنبه"
            Calendar.TUESDAY -> "سه‌شنبه"
            Calendar.WEDNESDAY -> "چهارشنبه"
            Calendar.THURSDAY -> "پنج‌شنبه"
            Calendar.FRIDAY -> "جمعه"
            else -> ""
        }
        return ShamsiDate(jy, jm, jd, dowName)
    }

    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gdm = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        val gy2 = if (gm > 2) gy + 1 else gy
        var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) +
            ((gy2 + 399) / 400) + gd + gdm[gm - 1]
        var jy = -1595 + (33 * (days / 12053))
        days %= 12053
        jy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            jy += (days - 1) / 365
            days = (days - 1) % 365
        }
        val jm: Int
        val jd: Int
        if (days < 186) {
            jm = 1 + (days / 31)
            jd = 1 + (days % 31)
        } else {
            jm = 7 + ((days - 186) / 30)
            jd = 1 + ((days - 186) % 30)
        }
        return Triple(jy, jm, jd)
    }

    fun recentMonths(currentYear: Int, currentMonth: Int, count: Int = 6): List<Pair<Int, Int>> {
        val result = ArrayList<Pair<Int, Int>>(count)
        var y = currentYear
        var m = currentMonth
        repeat(count) {
            result.add(0, y to m)
            m -= 1
            if (m < 1) {
                m = 12
                y -= 1
            }
        }
        return result
    }
}
