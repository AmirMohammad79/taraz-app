package com.example

import com.example.util.CurrencyUnit
import com.example.util.PersianFormatUtils
import com.example.util.ShamsiDateUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun gregorianToJalali_convertsCorrectly() {
    // 2026-11-18 -> 1405/08/27
    val (jy, jm, jd) = ShamsiDateUtils.gregorianToJalali(2026, 11, 18)
    assertEquals(1405, jy)
    assertEquals(8, jm)
    assertEquals(27, jd)

    // 2024-03-20 -> 1403/01/01 (Nowruz 1403)
    val (ny, nm, nd) = ShamsiDateUtils.gregorianToJalali(2024, 3, 20)
    assertEquals(1403, ny)
    assertEquals(1, nm)
    assertEquals(1, nd)
  }

  @Test
  fun persianFormatUtils_formatsCurrencyAndDigits() {
    assertEquals(
      "۱۲،۵۰۰،۰۰۰ تومان",
      PersianFormatUtils.formatCurrency(
        amountToman = 12_500_000L,
        unit = CurrencyUnit.TOMAN,
        usePersianDigits = true,
        includeUnit = true,
      ),
    )
    assertEquals(
      "۱۲۵،۰۰۰،۰۰۰ ریال",
      PersianFormatUtils.formatCurrency(
        amountToman = 12_500_000L,
        unit = CurrencyUnit.RIAL,
        usePersianDigits = true,
        includeUnit = true,
      ),
    )
    assertEquals(
      "+۱۲.۵٪",
      PersianFormatUtils.formatPercent(12.5, includePlus = true, usePersianDigits = true),
    )
  }
}
