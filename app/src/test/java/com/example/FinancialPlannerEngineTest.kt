package com.example

import com.example.domain.FinancialHealthLevel
import com.example.domain.FinancialPlannerEngine
import com.example.domain.ScenarioFeasibility
import com.example.util.ShamsiDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancialPlannerEngineTest {

    private val sampleDate = ShamsiDate(year = 1405, month = 8, day = 15, dayOfWeekName = "شنبه")

    @Test
    fun generatesDualScenariosForBalancedUser() {
        val report = FinancialPlannerEngine.generatePlan(
            monthlyInflowToman = 100_000_000L,
            monthlyOutflowToman = 75_000_000L,
            fallbackAverageInflowToman = 0L,
            categorySpends = mapOf(
                "مسکن و اجاره" to 35_000_000L,
                "خوراک و خرید" to 25_000_000L,
                "نرم‌افزار و اشتراک" to 15_000_000L,
            ),
            existingBudgets = emptyList(),
            totalCardBalanceToman = 200_000_000L,
            currentShamsiDate = sampleDate,
            userSavingsPercent = 40,
        )

        assertEquals(FinancialHealthLevel.BALANCED, report.healthLevel)
        assertEquals(100_000_000L, report.effectiveIncomeToman)
        assertEquals(40, report.userSelectedScenario.targetSavingsPercent)
        assertEquals(40_000_000L, report.userSelectedScenario.targetSavingsToman)
        assertEquals(60_000_000L, report.userSelectedScenario.allowedExpenseToman)
        assertEquals(15_000_000L, report.userSelectedScenario.expenseReductionNeededToman)
        assertTrue(report.appRecommendedScenario.isAppRecommended)
        assertTrue(report.appRecommendedScenario.targetSavingsPercent in 20..40)
        assertTrue(report.appRecommendedScenario.actionStepsFa.isNotEmpty())
        assertTrue(report.userSelectedScenario.actionStepsFa.isNotEmpty())
    }

    @Test
    fun handlesZeroAndHundredPercentSavingsAccurately() {
        val zeroPlan = FinancialPlannerEngine.generatePlan(
            monthlyInflowToman = 50_000_000L,
            monthlyOutflowToman = 40_000_000L,
            fallbackAverageInflowToman = 0L,
            categorySpends = mapOf("مسکن و اجاره" to 25_000_000L, "خوراک و خرید" to 15_000_000L),
            existingBudgets = emptyList(),
            totalCardBalanceToman = 60_000_000L,
            currentShamsiDate = sampleDate,
            userSavingsPercent = 0,
        )
        assertEquals(0L, zeroPlan.userSelectedScenario.targetSavingsToman)
        assertEquals(50_000_000L, zeroPlan.userSelectedScenario.allowedExpenseToman)

        val hundredPlan = FinancialPlannerEngine.generatePlan(
            monthlyInflowToman = 50_000_000L,
            monthlyOutflowToman = 40_000_000L,
            fallbackAverageInflowToman = 0L,
            categorySpends = mapOf("مسکن و اجاره" to 25_000_000L, "خوراک و خرید" to 15_000_000L),
            existingBudgets = emptyList(),
            totalCardBalanceToman = 60_000_000L,
            currentShamsiDate = sampleDate,
            userSavingsPercent = 100,
        )
        assertEquals(50_000_000L, hundredPlan.userSelectedScenario.targetSavingsToman)
        assertEquals(0L, hundredPlan.userSelectedScenario.allowedExpenseToman)
        assertEquals(ScenarioFeasibility.INCOME_REQUIRED, hundredPlan.userSelectedScenario.feasibility)
        assertTrue(hundredPlan.userSelectedScenario.extraIncomeNeededToman > 0L)
    }

    @Test
    fun detectsDeficitAndPrioritizesFlexibleCutsOverEssentials() {
        val deficitPlan = FinancialPlannerEngine.generatePlan(
            monthlyInflowToman = 60_000_000L,
            monthlyOutflowToman = 72_000_000L,
            fallbackAverageInflowToman = 0L,
            categorySpends = mapOf(
                "مسکن و اجاره" to 30_000_000L,
                "خوراک و خرید" to 26_000_000L,
                "نرم‌افزار و اشتراک" to 16_000_000L,
            ),
            existingBudgets = emptyList(),
            totalCardBalanceToman = 20_000_000L,
            currentShamsiDate = sampleDate,
            userSavingsPercent = 20,
        )

        assertEquals(FinancialHealthLevel.DEFICIT, deficitPlan.healthLevel)
        val allocs = deficitPlan.userSelectedScenario.categoryAllocations.associateBy { it.category }
        val rentCut = -(allocs["مسکن و اجاره"]?.deltaToman ?: 0L)
        val foodCut = -(allocs["خوراک و خرید"]?.deltaToman ?: 0L)
        assertTrue("Flexible spending should be cut more aggressively than rent", foodCut > rentCut)
    }
}
