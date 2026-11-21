package com.example.domain

import com.example.data.local.BudgetEntity
import com.example.data.local.BudgetIconKey
import com.example.util.CurrencyUnit
import com.example.util.PersianFormatUtils
import com.example.util.ShamsiDate
import kotlin.math.abs
import kotlin.math.roundToLong

enum class FinancialHealthLevel(val labelFa: String) {
    NO_INCOME("بدون درآمد ثبت‌شده"),
    DEFICIT("کسری بودجه (هزینه > درآمد)"),
    TIGHT("نزدیک مرز تعادل"),
    BALANCED("تراز سالم و متعادل"),
    STRONG("ذخیره‌سازی عالی"),
}

enum class ScenarioFeasibility(val labelFa: String) {
    COMFORTABLE("راحت و پایدار"),
    BALANCED("متعادل و بهینه"),
    CHALLENGING("چالشی ولی دست‌یافتنی"),
    EXTREME("بسیار فشرده"),
    INCOME_REQUIRED("نیازمند افزایش درآمد"),
}

enum class SpendPriority(val labelFa: String) {
    ESSENTIAL("ضروری و ثابت"),
    FLEXIBLE("قابل تعدیل"),
    INVESTMENT("سرمایه‌گذاری"),
}

data class CategoryPlanAllocation(
    val category: String,
    val iconKey: String,
    val priority: SpendPriority,
    val currentSpentToman: Long,
    val recommendedLimitToman: Long,
    val deltaToman: Long, // negative means cut needed, positive means room left
)

data class SavingsScenario(
    val titleFa: String,
    val subtitleFa: String,
    val isAppRecommended: Boolean,
    val targetSavingsPercent: Int,
    val targetSavingsToman: Long,
    val annualSavingsProjectionToman: Long,
    val allowedExpenseToman: Long,
    val dailySafeSpendToman: Long,
    val expenseReductionNeededToman: Long,
    val extraIncomeNeededToman: Long,
    val remainingRoomToman: Long,
    val feasibility: ScenarioFeasibility,
    val categoryAllocations: List<CategoryPlanAllocation>,
    val actionStepsFa: List<String>,
)

data class FinancialPlanReport(
    val effectiveIncomeToman: Long,
    val isUsingHistoricalIncome: Boolean,
    val currentExpenseToman: Long,
    val currentNetToman: Long,
    val currentSavingsPercent: Double,
    val daysRemainingInMonth: Int,
    val totalDaysInMonth: Int,
    val healthLevel: FinancialHealthLevel,
    val appRecommendedScenario: SavingsScenario,
    val userSelectedScenario: SavingsScenario,
)

object FinancialPlannerEngine {

    fun generatePlan(
        monthlyInflowToman: Long,
        monthlyOutflowToman: Long,
        fallbackAverageInflowToman: Long,
        categorySpends: Map<String, Long>,
        existingBudgets: List<BudgetEntity>,
        totalCardBalanceToman: Long,
        currentShamsiDate: ShamsiDate,
        userSavingsPercent: Int,
        currencyUnit: CurrencyUnit = CurrencyUnit.TOMAN,
        usePersianDigits: Boolean = true,
    ): FinancialPlanReport {
        val clampedUserPercent = userSavingsPercent.coerceIn(0, 100)
        val usingHistorical = monthlyInflowToman <= 0L && fallbackAverageInflowToman > 0L
        val effectiveIncome = when {
            monthlyInflowToman > 0L -> monthlyInflowToman
            fallbackAverageInflowToman > 0L -> fallbackAverageInflowToman
            else -> 0L
        }

        val totalDaysInMonth = when {
            currentShamsiDate.month <= 6 -> 31
            currentShamsiDate.month <= 11 -> 30
            else -> 29
        }
        val daysRemaining = (totalDaysInMonth - currentShamsiDate.day + 1).coerceAtLeast(1)

        val currentNet = effectiveIncome - monthlyOutflowToman
        val currentSavingsPct = if (effectiveIncome > 0L) {
            (currentNet.toDouble() / effectiveIncome.toDouble()) * 100.0
        } else {
            0.0
        }

        val healthLevel = when {
            effectiveIncome <= 0L -> FinancialHealthLevel.NO_INCOME
            monthlyOutflowToman > effectiveIncome -> FinancialHealthLevel.DEFICIT
            currentSavingsPct < 10.0 -> FinancialHealthLevel.TIGHT
            currentSavingsPct < 30.0 -> FinancialHealthLevel.BALANCED
            else -> FinancialHealthLevel.STRONG
        }

        // Build unified category map from actual spending + existing budgets
        val allCategories = LinkedHashSet<String>()
        categorySpends.keys.forEach { allCategories.add(it) }
        existingBudgets.forEach { allCategories.add(it.category) }
        if (allCategories.isEmpty() && effectiveIncome > 0L) {
            allCategories.addAll(
                listOf("مسکن و اجاره", "خوراک و خرید", "حمل‌ونقل", "بهداشت و درمان", "نرم‌افزار و اشتراک")
            )
        }

        val budgetByCategory = existingBudgets.associateBy { it.category }

        // Compute essential spend floor for smart recommendation
        val essentialCurrentSpend = allCategories
            .filter { classifyCategory(it) == SpendPriority.ESSENTIAL }
            .sumOf { categorySpends[it] ?: 0L }

        val recommendedPercent = computeSmartAppSavingsPercent(
            effectiveIncome = effectiveIncome,
            currentExpense = monthlyOutflowToman,
            essentialSpend = essentialCurrentSpend,
            currentSavingsPercent = currentSavingsPct,
        )

        val appScenario = buildScenario(
            titleFa = "پیشنهاد هوشمند تراز دقیق",
            subtitleFa = "متعادل‌سازی هوشمند بر اساس درآمد و هزینه‌های ضروری شما",
            isAppRecommended = true,
            targetPercent = recommendedPercent,
            effectiveIncome = effectiveIncome,
            currentExpense = monthlyOutflowToman,
            essentialSpend = essentialCurrentSpend,
            allCategories = allCategories.toList(),
            categorySpends = categorySpends,
            budgetByCategory = budgetByCategory,
            totalCardBalanceToman = totalCardBalanceToman,
            daysRemaining = daysRemaining,
            totalDaysInMonth = totalDaysInMonth,
            currencyUnit = currencyUnit,
            usePersianDigits = usePersianDigits,
        )

        val userScenario = buildScenario(
            titleFa = "سناریوی انتخابی شما (${
                PersianFormatUtils.formatNumber(clampedUserPercent.toLong(), usePersianDigits)
            }٪ پس‌انداز)",
            subtitleFa = "برنامه‌ریزی دقیق مخارج برای ذخیره دقیقاً ${
                PersianFormatUtils.formatNumber(clampedUserPercent.toLong(), usePersianDigits)
            } درصد از درآمد ماهانه",
            isAppRecommended = false,
            targetPercent = clampedUserPercent,
            effectiveIncome = effectiveIncome,
            currentExpense = monthlyOutflowToman,
            essentialSpend = essentialCurrentSpend,
            allCategories = allCategories.toList(),
            categorySpends = categorySpends,
            budgetByCategory = budgetByCategory,
            totalCardBalanceToman = totalCardBalanceToman,
            daysRemaining = daysRemaining,
            totalDaysInMonth = totalDaysInMonth,
            currencyUnit = currencyUnit,
            usePersianDigits = usePersianDigits,
        )

        return FinancialPlanReport(
            effectiveIncomeToman = effectiveIncome,
            isUsingHistoricalIncome = usingHistorical,
            currentExpenseToman = monthlyOutflowToman,
            currentNetToman = currentNet,
            currentSavingsPercent = currentSavingsPct,
            daysRemainingInMonth = daysRemaining,
            totalDaysInMonth = totalDaysInMonth,
            healthLevel = healthLevel,
            appRecommendedScenario = appScenario,
            userSelectedScenario = userScenario,
        )
    }

    private fun computeSmartAppSavingsPercent(
        effectiveIncome: Long,
        currentExpense: Long,
        essentialSpend: Long,
        currentSavingsPercent: Double,
    ): Int {
        if (effectiveIncome <= 0L) return 20
        val essentialRatio = (essentialSpend.toDouble() / effectiveIncome.toDouble()).coerceIn(0.0, 1.0)
        // Sustainable ceiling leaves essentials + at least 15% breathing room for daily life
        val maxHealthySavings = ((1.0 - essentialRatio - 0.15) * 100.0).toInt().coerceIn(10, 45)

        return when {
            // In deficit: prioritize achievable 10-15% stabilization target first
            currentExpense > effectiveIncome -> 15.coerceAtMost(maxHealthySavings).coerceAtLeast(10)
            // Tight budget (0-10%): gently step up to 20%
            currentSavingsPercent < 10.0 -> 20.coerceAtMost(maxHealthySavings).coerceAtLeast(12)
            // Healthy (10-30%): optimize around 25-30%
            currentSavingsPercent < 30.0 -> (currentSavingsPercent.toInt() + 5)
                .coerceIn(20, maxHealthySavings.coerceAtLeast(25))
            // Already strong (>=30%): keep strong sustainable wealth-building target (30-45%)
            else -> currentSavingsPercent.toInt().coerceIn(25, 45)
        }
    }

    private fun buildScenario(
        titleFa: String,
        subtitleFa: String,
        isAppRecommended: Boolean,
        targetPercent: Int,
        effectiveIncome: Long,
        currentExpense: Long,
        essentialSpend: Long,
        allCategories: List<String>,
        categorySpends: Map<String, Long>,
        budgetByCategory: Map<String, BudgetEntity>,
        totalCardBalanceToman: Long,
        daysRemaining: Int,
        totalDaysInMonth: Int,
        currencyUnit: CurrencyUnit,
        usePersianDigits: Boolean,
    ): SavingsScenario {
        val targetSavingsToman = ((effectiveIncome * targetPercent.toDouble()) / 100.0).roundToLong()
        val allowedExpenseToman = (effectiveIncome - targetSavingsToman).coerceAtLeast(0L)
        val reductionNeeded = (currentExpense - allowedExpenseToman).coerceAtLeast(0L)
        val remainingRoom = (allowedExpenseToman - currentExpense).coerceAtLeast(0L)
        val dailySafeSpend = if (remainingRoom > 0L) {
            remainingRoom / daysRemaining
        } else {
            allowedExpenseToman / totalDaysInMonth.coerceAtLeast(1)
        }

        // Minimum realistic essential floor (essentials can at most be trimmed by 10% short-term)
        val realisticEssentialFloor = (essentialSpend * 0.90).roundToLong()
        val extraIncomeNeeded = when {
            effectiveIncome <= 0L && currentExpense > 0L -> {
                if (targetPercent >= 100) currentExpense * 2L
                else ((currentExpense.toDouble() / ((100 - targetPercent) / 100.0)).roundToLong())
            }
            targetPercent == 100 && currentExpense > 0L -> currentExpense
            allowedExpenseToman < realisticEssentialFloor -> {
                // Extra income needed so essential floor + target savings both fit
                val neededIncomeForEssentials = if (targetPercent < 100) {
                    (realisticEssentialFloor.toDouble() / ((100 - targetPercent) / 100.0)).roundToLong()
                } else {
                    effectiveIncome + realisticEssentialFloor
                }
                (neededIncomeForEssentials - effectiveIncome).coerceAtLeast(realisticEssentialFloor - allowedExpenseToman)
            }
            else -> 0L
        }

        val feasibility = when {
            effectiveIncome <= 0L -> ScenarioFeasibility.INCOME_REQUIRED
            targetPercent == 100 && currentExpense > 0L -> ScenarioFeasibility.INCOME_REQUIRED
            extraIncomeNeeded > 0L -> ScenarioFeasibility.INCOME_REQUIRED
            reductionNeeded == 0L && targetPercent <= 25 -> ScenarioFeasibility.COMFORTABLE
            reductionNeeded == 0L -> ScenarioFeasibility.BALANCED
            effectiveIncome > 0L && (reductionNeeded.toDouble() / effectiveIncome.toDouble()) <= 0.15 -> ScenarioFeasibility.BALANCED
            effectiveIncome > 0L && (reductionNeeded.toDouble() / effectiveIncome.toDouble()) <= 0.30 -> ScenarioFeasibility.CHALLENGING
            else -> ScenarioFeasibility.EXTREME
        }

        val allocations = computeCategoryAllocations(
            allCategories = allCategories,
            categorySpends = categorySpends,
            budgetByCategory = budgetByCategory,
            allowedExpenseToman = allowedExpenseToman,
            currentExpense = currentExpense,
        )

        val steps = buildActionSteps(
            isAppRecommended = isAppRecommended,
            targetPercent = targetPercent,
            effectiveIncome = effectiveIncome,
            currentExpense = currentExpense,
            targetSavingsToman = targetSavingsToman,
            allowedExpenseToman = allowedExpenseToman,
            dailySafeSpend = dailySafeSpend,
            reductionNeeded = reductionNeeded,
            extraIncomeNeeded = extraIncomeNeeded,
            remainingRoom = remainingRoom,
            daysRemaining = daysRemaining,
            totalCardBalanceToman = totalCardBalanceToman,
            allocations = allocations,
            currencyUnit = currencyUnit,
            usePersianDigits = usePersianDigits,
        )

        return SavingsScenario(
            titleFa = titleFa,
            subtitleFa = subtitleFa,
            isAppRecommended = isAppRecommended,
            targetSavingsPercent = targetPercent,
            targetSavingsToman = targetSavingsToman,
            annualSavingsProjectionToman = targetSavingsToman * 12L,
            allowedExpenseToman = allowedExpenseToman,
            dailySafeSpendToman = dailySafeSpend,
            expenseReductionNeededToman = reductionNeeded,
            extraIncomeNeededToman = extraIncomeNeeded,
            remainingRoomToman = remainingRoom,
            feasibility = feasibility,
            categoryAllocations = allocations,
            actionStepsFa = steps,
        )
    }

    private fun computeCategoryAllocations(
        allCategories: List<String>,
        categorySpends: Map<String, Long>,
        budgetByCategory: Map<String, BudgetEntity>,
        allowedExpenseToman: Long,
        currentExpense: Long,
    ): List<CategoryPlanAllocation> {
        if (allCategories.isEmpty()) return emptyList()

        // Zero spendable allowance (e.g. 100% savings target)
        if (allowedExpenseToman <= 0L) {
            return allCategories.map { cat ->
                val spent = categorySpends[cat] ?: 0L
                CategoryPlanAllocation(
                    category = cat,
                    iconKey = budgetByCategory[cat]?.iconKey ?: inferIconKey(cat),
                    priority = classifyCategory(cat),
                    currentSpentToman = spent,
                    recommendedLimitToman = 0L,
                    deltaToman = -spent,
                )
            }.sortedByDescending { it.currentSpentToman }
        }

        // If user has no spending yet this month, distribute allowedExpense across standard ratios
        if (currentExpense <= 0L) {
            val weights = allCategories.associateWith { cat ->
                when (classifyCategory(cat)) {
                    SpendPriority.ESSENTIAL -> if (cat.contains("مسکن")) 0.40 else 0.14
                    SpendPriority.FLEXIBLE -> 0.16
                    SpendPriority.INVESTMENT -> 0.12
                }
            }
            val totalWeight = weights.values.sum().coerceAtLeast(0.01)
            return allCategories.map { cat ->
                val alloc = roundToNiceToman(((weights[cat] ?: 0.1) / totalWeight * allowedExpenseToman).roundToLong())
                CategoryPlanAllocation(
                    category = cat,
                    iconKey = budgetByCategory[cat]?.iconKey ?: inferIconKey(cat),
                    priority = classifyCategory(cat),
                    currentSpentToman = 0L,
                    recommendedLimitToman = alloc,
                    deltaToman = alloc,
                )
            }.sortedByDescending { it.recommendedLimitToman }
        }

        // Base allocations start from current spending (or budget baseline for 0-spend categories)
        val working = allCategories.associateWith { cat ->
            categorySpends[cat] ?: 0L
        }.toMutableMap()

        if (currentExpense > allowedExpenseToman) {
            var deficit = currentExpense - allowedExpenseToman

            // Step 1: Trim FLEXIBLE categories first (up to 65% of their current spend)
            val flexCats = allCategories.filter {
                classifyCategory(it) == SpendPriority.FLEXIBLE && (working[it] ?: 0L) > 0L
            }
            deficit = applyProportionalCut(working, flexCats, deficit, maxCutFraction = 0.65)

            // Step 2: Trim INVESTMENT outflows from expense bucket (since savings goal already covers wealth building)
            if (deficit > 0L) {
                val invCats = allCategories.filter {
                    classifyCategory(it) == SpendPriority.INVESTMENT && (working[it] ?: 0L) > 0L
                }
                deficit = applyProportionalCut(working, invCats, deficit, maxCutFraction = 0.85)
            }

            // Step 3: Gently trim ESSENTIAL categories if still needed (up to 15%, then deeper if extreme target)
            if (deficit > 0L) {
                val essCats = allCategories.filter {
                    classifyCategory(it) == SpendPriority.ESSENTIAL && (working[it] ?: 0L) > 0L
                }
                deficit = applyProportionalCut(working, essCats, deficit, maxCutFraction = 0.15)
            }

            // Step 4: If user selected an extreme target (e.g. 85-95%), scale remaining proportionally to fit allowedExpense
            if (deficit > 0L) {
                val remainingTotal = working.values.sum().coerceAtLeast(1L)
                val scale = (allowedExpenseToman.toDouble() / remainingTotal.toDouble()).coerceIn(0.0, 1.0)
                working.keys.forEach { cat ->
                    working[cat] = ((working[cat] ?: 0L) * scale).roundToLong()
                }
            }
        } else {
            // User is ALREADY within allowedExpense — preserve current spend and give slight cushion from remainingRoom
            val cushion = allowedExpenseToman - currentExpense
            val activeCats = allCategories.filter { (working[it] ?: 0L) > 0L }
            if (cushion > 0L && activeCats.isNotEmpty()) {
                val totalActive = activeCats.sumOf { working[it] ?: 0L }.coerceAtLeast(1L)
                activeCats.forEach { cat ->
                    val share = ((working[cat] ?: 0L).toDouble() / totalActive.toDouble() * cushion).roundToLong()
                    working[cat] = (working[cat] ?: 0L) + share
                }
            }
        }

        return allCategories
            .filter { (categorySpends[it] ?: 0L) > 0L || budgetByCategory.containsKey(it) }
            .map { cat ->
                val spent = categorySpends[cat] ?: 0L
                val rawTarget = working[cat] ?: 0L
                val recommended = if (rawTarget > 0L) roundToNiceToman(rawTarget) else 0L
                CategoryPlanAllocation(
                    category = cat,
                    iconKey = budgetByCategory[cat]?.iconKey ?: inferIconKey(cat),
                    priority = classifyCategory(cat),
                    currentSpentToman = spent,
                    recommendedLimitToman = recommended,
                    deltaToman = recommended - spent,
                )
            }
            .sortedByDescending { maxOf(it.currentSpentToman, it.recommendedLimitToman) }
    }

    private fun applyProportionalCut(
        working: MutableMap<String, Long>,
        categories: List<String>,
        neededCut: Long,
        maxCutFraction: Double,
    ): Long {
        if (neededCut <= 0L || categories.isEmpty()) return neededCut
        val totalCuttable = categories.sumOf { ((working[it] ?: 0L) * maxCutFraction).roundToLong() }
        if (totalCuttable <= 0L) return neededCut

        val actualTotalCut = minOf(neededCut, totalCuttable)
        val cutFactor = actualTotalCut.toDouble() / totalCuttable.toDouble()
        var cutApplied = 0L

        categories.forEach { cat ->
            val current = working[cat] ?: 0L
            val maxForCat = (current * maxCutFraction).roundToLong()
            val cutForCat = (maxForCat * cutFactor).roundToLong().coerceAtMost(current)
            working[cat] = (current - cutForCat).coerceAtLeast(0L)
            cutApplied += cutForCat
        }

        return (neededCut - cutApplied).coerceAtLeast(0L)
    }

    private fun buildActionSteps(
        isAppRecommended: Boolean,
        targetPercent: Int,
        effectiveIncome: Long,
        currentExpense: Long,
        targetSavingsToman: Long,
        allowedExpenseToman: Long,
        dailySafeSpend: Long,
        reductionNeeded: Long,
        extraIncomeNeeded: Long,
        remainingRoom: Long,
        daysRemaining: Int,
        totalCardBalanceToman: Long,
        allocations: List<CategoryPlanAllocation>,
        currencyUnit: CurrencyUnit,
        usePersianDigits: Boolean,
    ): List<String> {
        fun fmt(toman: Long): String = PersianFormatUtils.formatCurrency(
            amountToman = abs(toman),
            unit = currencyUnit,
            usePersianDigits = usePersianDigits,
            includeUnit = true,
        )

        val steps = mutableListOf<String>()

        if (effectiveIncome <= 0L) {
            steps += "برای محاسبه دقیق تراز دخل و خرج، ابتدا درآمد ماهانه خود را در بخش «ثبت درآمد» وارد کنید."
            if (currentExpense > 0L) {
                steps += "تا امروز ${fmt(currentExpense)} هزینه ثبت کرده‌اید؛ با ثبت درآمد، سقف خرج هر دسته دقیقاً تنظیم می‌شود."
            }
            return steps
        }

        if (targetPercent == 0) {
            steps += "با انتخاب ۰٪ پس‌انداز، کل درآمد (${fmt(effectiveIncome)}) به مخارج اختصاص می‌یابد و ذخیره‌ای برای شرایط اضطراری کنار گذاشته نمی‌شود."
            if (currentExpense > effectiveIncome) {
                steps += "هزینه فعلی شما ${fmt(currentExpense - effectiveIncome)} از درآمد بیشتر است؛ برای جلوگیری از کسری، همین مقدار را در هزینه‌های غیرضروری کاهش دهید."
            } else {
                steps += "تا پایان ماه ${fmt(remainingRoom)} فضای خرج باقی‌مانده دارید (حدود ${fmt(dailySafeSpend)} در روز)."
            }
            return steps
        }

        // Step 1: Pay-yourself-first automatic transfer advice
        steps += "بلافاصله پس از واریز درآمد، مبلغ ${fmt(targetSavingsToman)} (${
            PersianFormatUtils.formatNumber(targetPercent.toLong(), usePersianDigits)
        }٪) را به کارت پس‌انداز یا سپرده منتقل کنید تا با مخارج روزمره ترکیب نشود."

        // Step 2: Expense cut or safe remaining budget advice
        if (reductionNeeded > 0L) {
            val biggestCuts = allocations.filter { it.deltaToman < 0L }.sortedBy { it.deltaToman }.take(2)
            if (biggestCuts.isNotEmpty()) {
                val details = biggestCuts.joinToString(" و ") {
                    "«${it.category}» (${fmt(it.deltaToman)})"
                }
                steps += "برای رسیدن به این هدف لازم است مجموع هزینه‌ها ${fmt(reductionNeeded)} کمتر شود؛ بیشترین صرفه‌جویی از کاهش در $details به‌دست می‌آید."
            } else {
                steps += "مجموع مخارج ماهانه را حداکثر تا سقف ${fmt(allowedExpenseToman)} کنترل کنید."
            }
        } else {
            steps += "عالی! هزینه‌های فعلی شما از سقف مجاز کمتر است و تا پایان ماه (${
                PersianFormatUtils.formatNumber(daysRemaining.toLong(), usePersianDigits)
            } روز باقی‌مانده) می‌توانید روزانه تا ${fmt(dailySafeSpend)} با خیال راحت خرج کنید."
        }

        // Step 3: Honest income-boost guidance if target is aggressive (e.g., 70%-100%)
        if (extraIncomeNeeded > 0L) {
            steps += "چون درصد پس‌انداز انتخابی بالاست و هزینه‌های ثابت شما را محدود می‌کند، علاوه بر مدیریت خرج، افزایش درآمد ماهانه به میزان حدود ${fmt(extraIncomeNeeded)} این هدف را کاملاً عملی می‌کند."
        } else if (isAppRecommended && totalCardBalanceToman > 0L && allowedExpenseToman > 0L) {
            val runwayMonths = (totalCardBalanceToman.toDouble() / allowedExpenseToman.toDouble())
            val runwayRounded = (runwayMonths * 10.0).roundToLong() / 10.0
            steps += "با پایبندی به این برنامه، ظرف یک سال ${fmt(targetSavingsToman * 12L)} ذخیره می‌کنید و ذخیره فعلی کارت‌های شما پشتوانه حدود ${
                PersianFormatUtils.toPersianDigits(runwayRounded.toString())
            } ماه هزینه زندگی است."
        } else {
            steps += "با ادامه این روند در ۱۲ ماه آینده، مجموع پس‌انداز شما به ${fmt(targetSavingsToman * 12L)} خواهد رسید."
        }

        return steps
    }

    fun classifyCategory(category: String): SpendPriority {
        return when {
            category.contains("مسکن") ||
                category.contains("اجاره") ||
                category.contains("بهداشت") ||
                category.contains("درمان") ||
                category.contains("بیمه") ||
                category.contains("حمل") ||
                category.contains("قبض") -> SpendPriority.ESSENTIAL

            category.contains("سرمایه") ||
                category.contains("پس‌انداز") -> SpendPriority.INVESTMENT

            else -> SpendPriority.FLEXIBLE
        }
    }

    fun inferIconKey(category: String): String {
        return when {
            category.contains("مسکن") || category.contains("اجاره") -> BudgetIconKey.HOME.name
            category.contains("خوراک") || category.contains("خرید") -> BudgetIconKey.FOOD.name
            category.contains("حمل") || category.contains("خودرو") -> BudgetIconKey.TRANSPORT.name
            category.contains("نرم‌افزار") || category.contains("اشتراک") -> BudgetIconKey.TECH.name
            category.contains("بهداشت") || category.contains("درمان") -> BudgetIconKey.HEALTH.name
            category.contains("سرمایه") -> BudgetIconKey.INVEST.name
            else -> BudgetIconKey.GENERAL.name
        }
    }

    private fun roundToNiceToman(value: Long): Long {
        if (value <= 0L) return 0L
        return when {
            value >= 1_000_000L -> ((value + 50_000L) / 100_000L) * 100_000L
            value >= 100_000L -> ((value + 5_000L) / 10_000L) * 10_000L
            else -> value
        }
    }
}
