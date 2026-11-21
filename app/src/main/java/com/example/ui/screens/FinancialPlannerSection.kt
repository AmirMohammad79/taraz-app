package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CategoryPlanAllocation
import com.example.domain.FinancialHealthLevel
import com.example.domain.FinancialPlanReport
import com.example.domain.SavingsScenario
import com.example.domain.ScenarioFeasibility
import com.example.ui.components.CategoryGlassIconBadge
import com.example.ui.components.SoftGlassIconBadge
import com.example.ui.components.TarazCard
import com.example.ui.theme.TabularNumTextStyle
import com.example.ui.theme.TarazTheme
import com.example.util.CurrencyUnit
import com.example.util.PersianFormatUtils
import kotlin.math.abs
import kotlin.math.roundToInt

private val QUICK_SAVINGS_PRESETS = listOf(0, 10, 20, 30, 50, 75, 100)

@Composable
fun FinancialHealthDiagnosisCard(
    plan: FinancialPlanReport,
    currencyUnit: CurrencyUnit,
    usePersianDigits: Boolean,
    onAddIncome: () -> Unit,
) {
    val colors = TarazTheme.colors
    val (badgeBg, badgeFg, glowColor) = when (plan.healthLevel) {
        FinancialHealthLevel.DEFICIT -> Triple(colors.outflowBg, colors.outflowText, colors.outflow)
        FinancialHealthLevel.TIGHT -> Triple(colors.warningBg, colors.warningText, colors.warning)
        FinancialHealthLevel.BALANCED -> Triple(colors.activeNavBg, colors.techBlue, colors.techBlue)
        FinancialHealthLevel.STRONG -> Triple(colors.inflowBg, colors.inflowText, colors.inflow)
        FinancialHealthLevel.NO_INCOME -> Triple(colors.neutralBadgeBg, colors.neutralBadgeText, colors.techBlue)
    }

    TarazCard(
        modifier = Modifier.fillMaxWidth(),
        accentGlow = glowColor,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f),
            ) {
                SoftGlassIconBadge(
                    icon = Icons.Rounded.Insights,
                    tint = glowColor,
                    size = 40.dp,
                    iconSize = 21.dp,
                    cornerRadius = 13.dp,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "تحلیل واقعی دخل و خرج شما",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = if (plan.isUsingHistoricalIncome) {
                            "بر پایه میانگین درآمد ماه‌های قبل و هزینه‌های این ماه"
                        } else {
                            "محاسبه‌شده دقیقاً از تراکنش‌های واقعی ماه جاری"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(badgeBg)
                    .border(1.dp, badgeFg.copy(alpha = 0.35f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = plan.healthLevel.labelFa,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = badgeFg,
                    maxLines = 1,
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MiniDiagnosisTile(
                label = if (plan.isUsingHistoricalIncome) "میانگین درآمد" else "درآمد ماه",
                value = PersianFormatUtils.formatCurrency(plan.effectiveIncomeToman, currencyUnit, usePersianDigits),
                unit = currencyUnit.labelFa,
                valueColor = colors.inflowText,
                modifier = Modifier.weight(1f),
            )
            MiniDiagnosisTile(
                label = "هزینه ثبت‌شده",
                value = PersianFormatUtils.formatCurrency(plan.currentExpenseToman, currencyUnit, usePersianDigits),
                unit = currencyUnit.labelFa,
                valueColor = colors.outflowText,
                modifier = Modifier.weight(1f),
            )
            MiniDiagnosisTile(
                label = "نرخ پس‌انداز فعلی",
                value = PersianFormatUtils.formatPercent(plan.currentSavingsPercent, usePersianDigits = usePersianDigits),
                unit = "${PersianFormatUtils.formatNumber(plan.daysRemainingInMonth.toLong(), usePersianDigits)} روز مانده",
                valueColor = if (plan.currentNetToman >= 0L) colors.navyAnchor else colors.outflow,
                modifier = Modifier.weight(1f),
            )
        }

        if (plan.effectiveIncomeToman <= 0L) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onAddIncome,
                colors = ButtonDefaults.buttonColors(containerColor = colors.inflow, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("ثبت درآمد ماهانه برای شروع برنامه‌ریزی دقیق", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun MiniDiagnosisTile(
    label: String,
    value: String,
    unit: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.glassInnerSheen)
            .border(1.dp, colors.glassBorderHighlight.copy(alpha = 0.75f), RoundedCornerShape(12.dp))
            .padding(horizontal = 9.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = colors.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            style = TabularNumTextStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
            color = valueColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = colors.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun SavingsPercentSelectorCard(
    targetSavingsPercent: Int,
    effectiveIncomeToman: Long,
    currencyUnit: CurrencyUnit,
    usePersianDigits: Boolean,
    onPercentChange: (Int) -> Unit,
) {
    val colors = TarazTheme.colors
    val targetToman = (effectiveIncomeToman * targetSavingsPercent) / 100L

    TarazCard(
        modifier = Modifier.fillMaxWidth(),
        accentGlow = colors.techBlue,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f),
            ) {
                SoftGlassIconBadge(
                    icon = Icons.Rounded.Tune,
                    tint = colors.techBlue,
                    size = 38.dp,
                    iconSize = 20.dp,
                    cornerRadius = 12.dp,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "درصد پس‌انداز دلخواه شما",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = "از ۰٪ تا ۱۰۰٪ انتخاب کنید تا دو سناریو مقایسه شوند",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(colors.techBlue, colors.inflow),
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${PersianFormatUtils.formatNumber(targetSavingsPercent.toLong(), usePersianDigits)}٪",
                    style = TabularNumTextStyle.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Slider(
            value = targetSavingsPercent.toFloat(),
            onValueChange = { onPercentChange(it.roundToInt()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = colors.techBlue,
                activeTrackColor = colors.techBlue,
                inactiveTrackColor = colors.progressTrack,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("savings_percent_slider"),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "۰٪ (بدون ذخیره)",
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
            Text(
                text = "معادل ${
                    PersianFormatUtils.formatCurrency(
                        targetToman,
                        currencyUnit,
                        usePersianDigits,
                        includeUnit = true,
                    )
                } در ماه",
                style = TabularNumTextStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                color = colors.techBlue,
            )
            Text(
                text = "۱۰۰٪ (حداکثر)",
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            QUICK_SAVINGS_PRESETS.forEach { preset ->
                val selected = targetSavingsPercent == preset
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (selected) colors.techBlue else colors.glassInnerSheen,
                    border = BorderStroke(
                        1.dp,
                        if (selected) colors.techBlue else colors.glassBorderHighlight,
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onPercentChange(preset) }
                        .testTag("savings_preset_$preset"),
                ) {
                    Text(
                        text = "${PersianFormatUtils.formatNumber(preset.toLong(), usePersianDigits)}٪",
                        style = TabularNumTextStyle.copy(
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        ),
                        color = if (selected) Color.White else colors.textPrimary,
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun ScenarioComparisonSummaryCard(
    appScenario: SavingsScenario,
    userScenario: SavingsScenario,
    currencyUnit: CurrencyUnit,
    usePersianDigits: Boolean,
) {
    val colors = TarazTheme.colors
    TarazCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp),
    ) {
        Text(
            text = "مقایسه سریع دو سناریو",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(colors.tableHeaderSurface)
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "شاخص",
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "پیشنهاد اپ",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.inflowText,
                modifier = Modifier.weight(1.1f),
            )
            Text(
                text = "انتخاب شما",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.techBlue,
                modifier = Modifier.weight(1.1f),
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        CompareMetricRow(
            label = "درصد پس‌انداز",
            left = "${PersianFormatUtils.formatNumber(appScenario.targetSavingsPercent.toLong(), usePersianDigits)}٪",
            right = "${PersianFormatUtils.formatNumber(userScenario.targetSavingsPercent.toLong(), usePersianDigits)}٪",
        )
        HorizontalDivider(color = colors.border.copy(alpha = 0.6f))
        CompareMetricRow(
            label = "ذخیره ماهانه",
            left = PersianFormatUtils.formatCurrency(appScenario.targetSavingsToman, currencyUnit, usePersianDigits),
            right = PersianFormatUtils.formatCurrency(userScenario.targetSavingsToman, currencyUnit, usePersianDigits),
        )
        HorizontalDivider(color = colors.border.copy(alpha = 0.6f))
        CompareMetricRow(
            label = "سقف خرج ماه",
            left = PersianFormatUtils.formatCurrency(appScenario.allowedExpenseToman, currencyUnit, usePersianDigits),
            right = PersianFormatUtils.formatCurrency(userScenario.allowedExpenseToman, currencyUnit, usePersianDigits),
        )
        HorizontalDivider(color = colors.border.copy(alpha = 0.6f))
        CompareMetricRow(
            label = "ذخیره یک‌ساله",
            left = PersianFormatUtils.formatCurrency(appScenario.annualSavingsProjectionToman, currencyUnit, usePersianDigits),
            right = PersianFormatUtils.formatCurrency(userScenario.annualSavingsProjectionToman, currencyUnit, usePersianDigits),
        )
    }
}

@Composable
private fun CompareMetricRow(label: String, left: String, right: String) {
    val colors = TarazTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = left,
            style = TabularNumTextStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
            color = colors.inflowText,
            modifier = Modifier.weight(1.1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = right,
            style = TabularNumTextStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
            color = colors.techBlue,
            modifier = Modifier.weight(1.1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun SavingsScenarioCard(
    scenario: SavingsScenario,
    currencyUnit: CurrencyUnit,
    usePersianDigits: Boolean,
    onApplyScenario: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    val primaryAccent = if (scenario.isAppRecommended) colors.inflow else colors.techBlue
    val headerIcon = if (scenario.isAppRecommended) Icons.Rounded.AutoAwesome else Icons.Rounded.Savings

    val (feasBg, feasFg) = when (scenario.feasibility) {
        ScenarioFeasibility.COMFORTABLE -> colors.inflowBg to colors.inflowText
        ScenarioFeasibility.BALANCED -> colors.activeNavBg to colors.techBlue
        ScenarioFeasibility.CHALLENGING -> colors.warningBg to colors.warningText
        ScenarioFeasibility.EXTREME, ScenarioFeasibility.INCOME_REQUIRED -> colors.outflowBg to colors.outflowText
    }

    var showAllCategories by remember(scenario.targetSavingsPercent, scenario.isAppRecommended) {
        mutableStateOf(false)
    }

    TarazCard(
        modifier = modifier.testTag(
            if (scenario.isAppRecommended) "scenario_card_app" else "scenario_card_user"
        ),
        accentGlow = primaryAccent,
    ) {
        // Top tag row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(primaryAccent.copy(alpha = 0.14f))
                    .border(1.dp, primaryAccent.copy(alpha = 0.35f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Icon(
                    imageVector = headerIcon,
                    contentDescription = null,
                    tint = primaryAccent,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = if (scenario.isAppRecommended) "سناریو ۱ • پیشنهاد اپ" else "سناریو ۲ • انتخاب شما",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = primaryAccent,
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(feasBg)
                    .padding(horizontal = 9.dp, vertical = 4.dp),
            ) {
                Text(
                    text = scenario.feasibility.labelFa,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = feasFg,
                    maxLines = 1,
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = scenario.titleFa,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
        Text(
            text = scenario.subtitleFa,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2x2 Key Scenario Metrics
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ScenarioMetricBox(
                    label = "پس‌انداز ماهانه (${
                        PersianFormatUtils.formatNumber(scenario.targetSavingsPercent.toLong(), usePersianDigits)
                    }٪)",
                    value = PersianFormatUtils.formatCurrency(scenario.targetSavingsToman, currencyUnit, usePersianDigits),
                    unit = currencyUnit.labelFa,
                    accent = primaryAccent,
                    modifier = Modifier.weight(1f),
                )
                ScenarioMetricBox(
                    label = "سقف کل خرج ماه",
                    value = PersianFormatUtils.formatCurrency(scenario.allowedExpenseToman, currencyUnit, usePersianDigits),
                    unit = currencyUnit.labelFa,
                    accent = colors.navyAnchor,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ScenarioMetricBox(
                    label = "سقف خرج روزانه مجاز",
                    value = PersianFormatUtils.formatCurrency(scenario.dailySafeSpendToman, currencyUnit, usePersianDigits),
                    unit = "${currencyUnit.labelFa} / روز",
                    accent = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                ScenarioMetricBox(
                    label = "ذخیره ۱۲ ماه آینده",
                    value = PersianFormatUtils.formatCurrency(scenario.annualSavingsProjectionToman, currencyUnit, usePersianDigits),
                    unit = currencyUnit.labelFa,
                    accent = colors.inflowText,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Deficit / Extra Income Needed Callout if applicable
        if (scenario.expenseReductionNeededToman > 0L || scenario.extraIncomeNeededToman > 0L) {
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (scenario.extraIncomeNeededToman > 0L) colors.outflowBg else colors.warningBg)
                    .border(
                        1.dp,
                        (if (scenario.extraIncomeNeededToman > 0L) colors.outflow else colors.warning).copy(alpha = 0.35f),
                        RoundedCornerShape(12.dp),
                    )
                    .padding(11.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                if (scenario.expenseReductionNeededToman > 0L) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WarningAmber,
                            contentDescription = null,
                            tint = if (scenario.extraIncomeNeededToman > 0L) colors.outflow else colors.warning,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "نیاز به کاهش هزینه: ${
                                PersianFormatUtils.formatCurrency(
                                    scenario.expenseReductionNeededToman,
                                    currencyUnit,
                                    usePersianDigits,
                                    includeUnit = true,
                                )
                            }",
                            style = TabularNumTextStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                            color = if (scenario.extraIncomeNeededToman > 0L) colors.outflowText else colors.warningText,
                        )
                    }
                }
                if (scenario.extraIncomeNeededToman > 0L) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.TrendingUp,
                            contentDescription = null,
                            tint = colors.outflow,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "درآمد اضافی لازم برای حفظ هزینه‌های ثابت: ${
                                PersianFormatUtils.formatCurrency(
                                    scenario.extraIncomeNeededToman,
                                    currencyUnit,
                                    usePersianDigits,
                                    includeUnit = true,
                                )
                            }",
                            style = TabularNumTextStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                            color = colors.outflowText,
                        )
                    }
                }
            }
        }

        // Personalized Action Steps
        if (scenario.actionStepsFa.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "برنامه اجرایی پیشنهادی",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                scenario.actionStepsFa.forEach { step ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(primaryAccent),
                        )
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textPrimary,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        // Category-by-Category Budget Breakdown
        if (scenario.categoryAllocations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = colors.border)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "سقف پیشنهادی هر دسته‌بندی",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                )
                if (scenario.categoryAllocations.size > 3) {
                    Text(
                        text = if (showAllCategories) "نمایش کمتر" else "نمایش همه (${
                            PersianFormatUtils.formatNumber(scenario.categoryAllocations.size.toLong(), usePersianDigits)
                        })",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.techBlue,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showAllCategories = !showAllCategories }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val displayed = if (showAllCategories) {
                scenario.categoryAllocations
            } else {
                scenario.categoryAllocations.take(3)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                displayed.forEach { alloc ->
                    CategoryAllocationRow(
                        alloc = alloc,
                        currencyUnit = currencyUnit,
                        usePersianDigits = usePersianDigits,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Actionable Apply Button
        if (scenario.isAppRecommended) {
            Button(
                onClick = onApplyScenario,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.inflow,
                    contentColor = Color.White,
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_app_scenario_btn"),
            ) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("اعمال پیشنهاد هوشمند روی بودجه‌ها", style = MaterialTheme.typography.labelLarge)
            }
        } else {
            OutlinedButton(
                onClick = onApplyScenario,
                border = BorderStroke(1.2.dp, colors.techBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.techBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_user_scenario_btn"),
            ) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("اعمال سناریوی انتخابی من روی بودجه‌ها", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun ScenarioMetricBox(
    label: String,
    value: String,
    unit: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.glassInnerSheen)
            .border(1.dp, colors.glassBorderHighlight.copy(alpha = 0.75f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = colors.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            style = TabularNumTextStyle.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
            color = accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = colors.textMuted,
            maxLines = 1,
        )
    }
}

@Composable
private fun CategoryAllocationRow(
    alloc: CategoryPlanAllocation,
    currencyUnit: CurrencyUnit,
    usePersianDigits: Boolean,
) {
    val colors = TarazTheme.colors
    val needsCut = alloc.deltaToman < 0L
    val deltaLabel = when {
        alloc.deltaToman < 0L -> "کاهش ${
            PersianFormatUtils.formatCurrency(abs(alloc.deltaToman), currencyUnit, usePersianDigits)
        }"
        alloc.deltaToman > 0L -> "سقف آزاد +${
            PersianFormatUtils.formatCurrency(alloc.deltaToman, currencyUnit, usePersianDigits)
        }"
        else -> "بدون تغییر"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.glassInnerSheen)
            .border(1.dp, colors.glassBorderHighlight.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CategoryGlassIconBadge(
                category = alloc.category,
                isIncome = false,
                size = 32.dp,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alloc.category,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "فعلی: ${
                        PersianFormatUtils.formatCurrency(alloc.currentSpentToman, currencyUnit, usePersianDigits)
                    } • ${alloc.priority.labelFa}",
                    style = TabularNumTextStyle.copy(fontSize = 10.sp),
                    color = colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "سقف: ${
                    PersianFormatUtils.formatCurrency(alloc.recommendedLimitToman, currencyUnit, usePersianDigits)
                }",
                style = TabularNumTextStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                color = colors.navyAnchor,
                maxLines = 1,
            )
            Text(
                text = deltaLabel,
                style = TabularNumTextStyle.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                color = if (needsCut) colors.outflowText else colors.inflowText,
                maxLines = 1,
            )
        }
    }
}
