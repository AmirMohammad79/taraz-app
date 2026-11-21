package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.DonutLarge
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BudgetEntity
import com.example.data.local.BudgetIconKey
import com.example.domain.SavingsScenario
import com.example.ui.components.NeutralTagBadge
import com.example.ui.components.RtlBudgetProgressBar
import com.example.ui.components.SoftGlassIconBadge
import com.example.ui.components.TarazCard
import com.example.ui.theme.TabularNumTextStyle
import com.example.ui.theme.TarazTheme
import com.example.ui.viewmodel.BudgetProgressUi
import com.example.ui.viewmodel.BudgetsSubTab
import com.example.ui.viewmodel.TarazUiState
import com.example.util.PersianFormatUtils

@Composable
fun BudgetsScreen(
    uiState: TarazUiState,
    isExpanded: Boolean,
    isCompact: Boolean,
    onSelectSubTab: (BudgetsSubTab) -> Unit,
    onTargetSavingsPercentChange: (Int) -> Unit,
    onApplyScenario: (SavingsScenario) -> Unit,
    onAddIncome: () -> Unit,
    onAddBudget: () -> Unit,
    onEditBudget: (BudgetEntity) -> Unit,
    onDeleteBudget: (Long) -> Unit,
) {
    val colors = TarazTheme.colors
    val horizontalPad = if (isCompact) 14.dp else 24.dp

    val totalLimit = uiState.budgets.sumOf { it.budget.monthlyLimitToman }
    val totalSpent = uiState.budgets.sumOf { it.spentToman }
    val totalRatio = if (totalLimit > 0L) (totalSpent.toDouble() / totalLimit.toDouble()).toFloat() else 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = horizontalPad,
            end = horizontalPad,
            top = 16.dp,
            bottom = if (isCompact) 110.dp else 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Screen Header + Sub-Tab Switcher
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (uiState.budgetsSubTab == BudgetsSubTab.PLANNER) {
                                "برنامه‌ریز هوشمند دخل و خرج"
                            } else {
                                "مدیریت بودجه ماهانه"
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            color = colors.textPrimary,
                        )
                        Text(
                            text = if (uiState.budgetsSubTab == BudgetsSubTab.PLANNER) {
                                "متعادل‌سازی درآمد و هزینه با ۲ سناریوی پس‌انداز (۰ تا ۱۰۰٪)"
                            } else {
                                "سقف مخارج ${uiState.currentShamsiDate.monthName} و کنترل انحراف بودجه"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted,
                        )
                    }
                    if (uiState.budgetsSubTab == BudgetsSubTab.LIMITS) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = onAddBudget,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.techBlue,
                                contentColor = Color.White,
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                            modifier = Modifier.testTag("add_budget_button"),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("بودجه جدید", style = MaterialTheme.typography.labelLarge, maxLines = 1)
                        }
                    }
                }

                // Segmented Glass Sub-Tab Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.glassSurfaceTop)
                        .border(1.dp, colors.glassBorderHighlight, RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    BudgetsSubTab.entries.forEach { tab ->
                        val selected = uiState.budgetsSubTab == tab
                        val icon = if (tab == BudgetsSubTab.PLANNER) Icons.Rounded.AutoAwesome else Icons.Rounded.DonutLarge
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) colors.techBlue else Color.Transparent)
                                .clickable { onSelectSubTab(tab) }
                                .padding(vertical = 10.dp, horizontal = 8.dp)
                                .testTag("budgets_subtab_${tab.name}"),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (selected) Color.White else colors.textMuted,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.labelFa,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                ),
                                color = if (selected) Color.White else colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }

        if (uiState.budgetsSubTab == BudgetsSubTab.PLANNER) {
            val plan = uiState.financialPlan
            if (plan != null) {
                item {
                    FinancialHealthDiagnosisCard(
                        plan = plan,
                        currencyUnit = uiState.currencyUnit,
                        usePersianDigits = uiState.usePersianDigits,
                        onAddIncome = onAddIncome,
                    )
                }

                item {
                    SavingsPercentSelectorCard(
                        targetSavingsPercent = uiState.targetSavingsPercent,
                        effectiveIncomeToman = plan.effectiveIncomeToman,
                        currencyUnit = uiState.currencyUnit,
                        usePersianDigits = uiState.usePersianDigits,
                        onPercentChange = onTargetSavingsPercentChange,
                    )
                }

                item {
                    ScenarioComparisonSummaryCard(
                        appScenario = plan.appRecommendedScenario,
                        userScenario = plan.userSelectedScenario,
                        currencyUnit = uiState.currencyUnit,
                        usePersianDigits = uiState.usePersianDigits,
                    )
                }

                if (isExpanded) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            SavingsScenarioCard(
                                scenario = plan.appRecommendedScenario,
                                currencyUnit = uiState.currencyUnit,
                                usePersianDigits = uiState.usePersianDigits,
                                onApplyScenario = { onApplyScenario(plan.appRecommendedScenario) },
                                modifier = Modifier.weight(1f),
                            )
                            SavingsScenarioCard(
                                scenario = plan.userSelectedScenario,
                                currencyUnit = uiState.currencyUnit,
                                usePersianDigits = uiState.usePersianDigits,
                                onApplyScenario = { onApplyScenario(plan.userSelectedScenario) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                } else {
                    item {
                        SavingsScenarioCard(
                            scenario = plan.appRecommendedScenario,
                            currencyUnit = uiState.currencyUnit,
                            usePersianDigits = uiState.usePersianDigits,
                            onApplyScenario = { onApplyScenario(plan.appRecommendedScenario) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    item {
                        SavingsScenarioCard(
                            scenario = plan.userSelectedScenario,
                            currencyUnit = uiState.currencyUnit,
                            usePersianDigits = uiState.usePersianDigits,
                            onApplyScenario = { onApplyScenario(plan.userSelectedScenario) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        } else {
            // Overall Monthly Budget Summary
            item {
                TarazCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "جمع کل بودجه (${uiState.currentShamsiDate.monthName})",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.textMuted,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = PersianFormatUtils.formatCurrency(
                                    totalSpent,
                                    uiState.currencyUnit,
                                    uiState.usePersianDigits,
                                ),
                                style = MaterialTheme.typography.headlineLarge.merge(
                                    TabularNumTextStyle.copy(
                                        fontSize = if (isCompact) 20.sp else 24.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                ),
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = "از سقف ${
                                    PersianFormatUtils.formatCurrency(
                                        totalLimit,
                                        uiState.currencyUnit,
                                        uiState.usePersianDigits,
                                        includeUnit = true,
                                    )
                                }",
                                style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum"),
                                color = colors.textMuted,
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        val pctColor = when {
                            totalRatio <= 0.75f -> colors.inflowText
                            totalRatio <= 0.90f -> colors.warningText
                            else -> colors.outflowText
                        }
                        val pctBg = when {
                            totalRatio <= 0.75f -> colors.inflowBg
                            totalRatio <= 0.90f -> colors.warningBg
                            else -> colors.outflowBg
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = pctBg,
                        ) {
                            Text(
                                text = PersianFormatUtils.formatPercent(
                                    totalRatio * 100.0,
                                    usePersianDigits = uiState.usePersianDigits,
                                ),
                                style = TabularNumTextStyle.copy(fontWeight = FontWeight.Bold),
                                color = pctColor,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    RtlBudgetProgressBar(ratio = totalRatio)
                }
            }

            if (uiState.budgets.isEmpty()) {
                item {
                    TarazCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            SoftGlassIconBadge(
                                icon = Icons.Rounded.DonutLarge,
                                tint = colors.techBlue,
                                size = 52.dp,
                                iconSize = 26.dp,
                                cornerRadius = 16.dp,
                            )
                            Text(
                                text = "هنوز ردیف بودجه‌ای ثبت نشده است",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textPrimary,
                            )
                            Button(
                                onClick = onAddBudget,
                                colors = ButtonDefaults.buttonColors(containerColor = colors.techBlue),
                            ) {
                                Text("تعریف اولین بودجه", color = Color.White)
                            }
                        }
                    }
                }
            } else if (isExpanded) {
                val rows = uiState.budgets.chunked(2)
                items(rows) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        BudgetCategoryCard(
                            item = pair[0],
                            uiState = uiState,
                            isCompact = false,
                            onEdit = { onEditBudget(pair[0].budget) },
                            onDelete = { onDeleteBudget(pair[0].budget.id) },
                            modifier = Modifier.weight(1f),
                        )
                        if (pair.size > 1) {
                            BudgetCategoryCard(
                                item = pair[1],
                                uiState = uiState,
                                isCompact = false,
                                onEdit = { onEditBudget(pair[1].budget) },
                                onDelete = { onDeleteBudget(pair[1].budget.id) },
                                modifier = Modifier.weight(1f),
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                items(uiState.budgets, key = { it.budget.id }) { item ->
                    BudgetCategoryCard(
                        item = item,
                        uiState = uiState,
                        isCompact = isCompact,
                        onEdit = { onEditBudget(item.budget) },
                        onDelete = { onDeleteBudget(item.budget.id) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun BudgetCategoryCard(
    item: BudgetProgressUi,
    uiState: TarazUiState,
    isCompact: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    val remaining = item.budget.monthlyLimitToman - item.spentToman
    val isOver = remaining < 0L

    TarazCard(
        onClick = onEdit,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val (badgeIcon, badgeTint) = iconAndTintForBudgetKey(item.budget.iconKey)
                SoftGlassIconBadge(
                    icon = badgeIcon,
                    tint = badgeTint,
                    contentDescription = item.budget.category,
                    size = 42.dp,
                    iconSize = 21.dp,
                    cornerRadius = 13.dp,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.budget.category,
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val wsLabel = if (item.budget.workspace == "BUSINESS") "کسب‌وکار" else "شخصی"
                    NeutralTagBadge(text = wsLabel)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "ویرایش بودجه",
                        tint = colors.textMuted,
                        modifier = Modifier.size(18.dp),
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "حذف بودجه",
                        tint = colors.outflow,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        RtlBudgetProgressBar(ratio = item.ratio)
        Spacer(modifier = Modifier.height(10.dp))

        val spentLabel = "مصرف‌شده: ${
            PersianFormatUtils.formatCurrency(
                item.spentToman,
                uiState.currencyUnit,
                uiState.usePersianDigits,
                includeUnit = true,
            )
        }"
        val remainLabel = if (isOver) {
            "مازاد مصرف: ${
                PersianFormatUtils.formatCurrency(
                    -remaining,
                    uiState.currencyUnit,
                    uiState.usePersianDigits,
                    includeUnit = true,
                )
            }"
        } else {
            "باقی‌مانده: ${
                PersianFormatUtils.formatCurrency(
                    remaining,
                    uiState.currencyUnit,
                    uiState.usePersianDigits,
                    includeUnit = true,
                )
            }"
        }

        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = spentLabel,
                    style = TabularNumTextStyle.copy(fontSize = 12.sp),
                    color = colors.textPrimary,
                )
                Text(
                    text = remainLabel,
                    style = TabularNumTextStyle.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = if (isOver) colors.outflowText else colors.inflowText,
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = spentLabel,
                    style = TabularNumTextStyle.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize),
                    color = colors.textPrimary,
                )
                Text(
                    text = remainLabel,
                    style = TabularNumTextStyle.copy(
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = if (isOver) colors.outflowText else colors.inflowText,
                )
            }
        }
    }
}

@Composable
private fun iconAndTintForBudgetKey(key: String): Pair<ImageVector, Color> {
    val colors = TarazTheme.colors
    return when (BudgetIconKey.entries.firstOrNull { it.name == key }) {
        BudgetIconKey.HOME -> Icons.Rounded.Home to Color(0xFF6366F1)
        BudgetIconKey.FOOD -> Icons.Rounded.ShoppingBag to colors.warning
        BudgetIconKey.TRANSPORT -> Icons.Rounded.DirectionsCar to Color(0xFF06B6D4)
        BudgetIconKey.TECH -> Icons.Rounded.Devices to colors.techBlue
        BudgetIconKey.HEALTH -> Icons.Rounded.Favorite to colors.outflow
        BudgetIconKey.INVEST -> Icons.Rounded.TrendingUp to colors.inflow
        else -> Icons.Rounded.DonutLarge to colors.techBlue
    }
}
