package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.data.local.AccountEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionStatus
import com.example.ui.components.AddMiniBankCardTile
import com.example.ui.components.CardSourcePill
import com.example.ui.components.CashFlowBarChart
import com.example.ui.components.CategoryGlassIconBadge
import com.example.ui.components.MiniBankCardItem
import com.example.ui.components.NeutralTagBadge
import com.example.ui.components.RtlBudgetProgressBar
import com.example.ui.components.SoftGlassIconBadge
import com.example.ui.components.StatSummaryCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.TarazCard
import com.example.ui.theme.TabularNumTextStyle
import com.example.ui.theme.TarazTheme
import com.example.ui.viewmodel.TarazDestination
import com.example.ui.viewmodel.TarazUiState
import com.example.util.PersianFormatUtils

@Composable
fun DashboardScreen(
    uiState: TarazUiState,
    isExpanded: Boolean,
    isCompact: Boolean,
    onNavigate: (TarazDestination) -> Unit,
    onEditTransaction: (TransactionEntity) -> Unit,
    onOpenNewTransaction: (isIncome: Boolean?, accountId: Long?) -> Unit,
    onOpenAddCard: () -> Unit,
    onOpenTransfer: () -> Unit,
    onOpenCardLedger: (Long) -> Unit,
    onOpenSmartPlanner: () -> Unit,
) {
    val colors = TarazTheme.colors
    val horizontalPad = if (isCompact) 14.dp else 24.dp
    val accountMap = remember(uiState.accounts) { uiState.accounts.associateBy(AccountEntity::id) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = horizontalPad,
            end = horizontalPad,
            top = 16.dp,
            bottom = if (isCompact) 110.dp else 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        // Quick Operational Actions Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                QuickActionGlassTile(
                    label = "ثبت هزینه",
                    icon = Icons.Rounded.ArrowUpward,
                    tint = colors.outflow,
                    onClick = { onOpenNewTransaction(false, null) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_expense_btn"),
                )
                QuickActionGlassTile(
                    label = "ثبت درآمد",
                    icon = Icons.Rounded.ArrowDownward,
                    tint = colors.inflow,
                    onClick = { onOpenNewTransaction(true, null) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_income_btn"),
                )
                QuickActionGlassTile(
                    label = "کارت به کارت",
                    icon = Icons.Rounded.SwapHoriz,
                    tint = colors.techBlue,
                    onClick = onOpenTransfer,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_transfer_btn"),
                )
            }
        }

        // Smart Financial Balancing & Dual-Scenario Savings Banner
        uiState.financialPlan?.let { plan ->
            item {
                val appPct = plan.appRecommendedScenario.targetSavingsPercent
                val userPct = plan.userSelectedScenario.targetSavingsPercent
                val accent = when (plan.healthLevel) {
                    com.example.domain.FinancialHealthLevel.DEFICIT -> colors.outflow
                    com.example.domain.FinancialHealthLevel.TIGHT -> colors.warning
                    else -> colors.inflow
                }
                TarazCard(
                    onClick = onOpenSmartPlanner,
                    accentGlow = accent,
                    contentPadding = PaddingValues(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_smart_planner_card"),
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
                            SoftGlassIconBadge(
                                icon = Icons.Rounded.AutoAwesome,
                                tint = accent,
                                size = 40.dp,
                                iconSize = 21.dp,
                                cornerRadius = 13.dp,
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "برنامه‌ریز هوشمند دخل و خرج",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = colors.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "${plan.healthLevel.labelFa} • پیشنهاد اپ ${
                                        PersianFormatUtils.formatNumber(appPct.toLong(), uiState.usePersianDigits)
                                    }٪ | هدف شما ${
                                        PersianFormatUtils.formatNumber(userPct.toLong(), uiState.usePersianDigits)
                                    }٪",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.textMuted,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Rounded.ChevronLeft,
                            contentDescription = "مشاهده ۲ سناریو",
                            tint = colors.techBlue,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }
        }

        // 4 Responsive Stat Summary Cards
        item {
            if (isExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    BalanceStatCard(uiState, isCompact = false, modifier = Modifier.weight(1f))
                    InflowStatCard(uiState, isCompact = false, modifier = Modifier.weight(1f))
                    OutflowStatCard(uiState, isCompact = false, modifier = Modifier.weight(1f))
                    SavingsStatCard(uiState, isCompact = false, modifier = Modifier.weight(1f))
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        BalanceStatCard(uiState, isCompact = isCompact, modifier = Modifier.weight(1f))
                        InflowStatCard(uiState, isCompact = isCompact, modifier = Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutflowStatCard(uiState, isCompact = isCompact, modifier = Modifier.weight(1f))
                        SavingsStatCard(uiState, isCompact = isCompact, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // My Bank Cards Horizontal Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "کارت‌های بانکی من",
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.textPrimary,
                        )
                        Text(
                            text = "موجودی لحظه‌ای هر کارت — برای مشاهده گردش روی کارت بزنید",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    TextButton(
                        onClick = { onNavigate(TarazDestination.ACCOUNTS) },
                        modifier = Modifier.testTag("manage_cards_shortcut"),
                    ) {
                        Text(
                            text = "مدیریت کارت‌ها",
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.techBlue,
                        )
                        Icon(
                            imageVector = Icons.Rounded.ChevronLeft,
                            contentDescription = null,
                            tint = colors.techBlue,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 2.dp),
                ) {
                    items(uiState.accountSummaries, key = { it.account.id }) { summary ->
                        MiniBankCardItem(
                            summary = summary,
                            currencyUnit = uiState.currencyUnit,
                            usePersianDigits = uiState.usePersianDigits,
                            onClick = { onOpenCardLedger(summary.account.id) },
                        )
                    }
                    item {
                        AddMiniBankCardTile(onClick = onOpenAddCard)
                    }
                }
            }
        }

        // Cash Flow Analytical Chart + Budget Health Snapshot
        item {
            if (isExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    CashFlowCard(uiState, Modifier.weight(7f))
                    BudgetSnapshotCard(
                        uiState = uiState,
                        onViewAll = { onNavigate(TarazDestination.BUDGETS) },
                        modifier = Modifier.weight(5f),
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CashFlowCard(uiState, Modifier.fillMaxWidth())
                    BudgetSnapshotCard(
                        uiState = uiState,
                        onViewAll = { onNavigate(TarazDestination.BUDGETS) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        // Recent Transactions Preview
        item {
            TarazCard(
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "آخرین تراکنش‌ها",
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.textPrimary,
                        )
                        Text(
                            text = "۶ تراکنش اخیر با مشخصات کارت بانکی",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    TextButton(
                        onClick = { onNavigate(TarazDestination.LEDGER) },
                        modifier = Modifier.testTag("view_all_ledger_button"),
                    ) {
                        Text(
                            text = "مشاهده همه",
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.techBlue,
                        )
                        Icon(
                            imageVector = Icons.Rounded.ChevronLeft,
                            contentDescription = null,
                            tint = colors.techBlue,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                if (!isCompact) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.tableHeaderSurface)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "شرح و کارت",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textMuted,
                            modifier = Modifier.weight(2.2f),
                        )
                        Text(
                            text = "دسته‌بندی",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textMuted,
                            modifier = Modifier.weight(1.2f),
                        )
                        Text(
                            text = "تاریخ",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textMuted,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "وضعیت",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textMuted,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "مبلغ (${uiState.currencyUnit.labelFa})",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textMuted,
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = colors.border)

                val recent = uiState.allTransactions.take(6)
                if (recent.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "هنوز تراکنشی ثبت نشده است",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textMuted,
                        )
                        TextButton(onClick = { onOpenNewTransaction(null, null) }) {
                            Text("ثبت اولین تراکنش", color = colors.techBlue)
                        }
                    }
                } else {
                    recent.forEachIndexed { idx, tx ->
                        RecentTransactionRow(
                            tx = tx,
                            account = accountMap[tx.accountId],
                            isCompact = isCompact,
                            uiState = uiState,
                            onClick = { onEditTransaction(tx) },
                        )
                        if (idx < recent.lastIndex) {
                            HorizontalDivider(thickness = 1.dp, color = colors.border)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionGlassTile(
    label: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = colors.glassSurfaceTop,
        border = BorderStroke(1.dp, colors.glassBorderHighlight),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SoftGlassIconBadge(
                icon = icon,
                tint = tint,
                size = 36.dp,
                iconSize = 19.dp,
                cornerRadius = 11.dp,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun BalanceStatCard(
    uiState: TarazUiState,
    isCompact: Boolean,
    modifier: Modifier = Modifier,
) {
    StatSummaryCard(
        title = "موجودی کل کارت‌ها",
        valueToman = uiState.totalBalanceToman,
        currencyUnit = uiState.currencyUnit,
        usePersianDigits = uiState.usePersianDigits,
        icon = Icons.Rounded.AccountBalanceWallet,
        iconTint = TarazTheme.colors.techBlue,
        trendPercent = uiState.balanceTrendPercent,
        isPositiveTrendGood = true,
        isCompact = isCompact,
        modifier = modifier,
    )
}

@Composable
private fun InflowStatCard(
    uiState: TarazUiState,
    isCompact: Boolean,
    modifier: Modifier = Modifier,
) {
    StatSummaryCard(
        title = "درآمد ${uiState.currentShamsiDate.monthName}",
        valueToman = uiState.monthlyInflowToman,
        currencyUnit = uiState.currencyUnit,
        usePersianDigits = uiState.usePersianDigits,
        icon = Icons.Rounded.TrendingUp,
        iconTint = TarazTheme.colors.inflow,
        trendPercent = uiState.inflowTrendPercent,
        isPositiveTrendGood = true,
        accentColor = TarazTheme.colors.inflowText,
        isCompact = isCompact,
        modifier = modifier,
    )
}

@Composable
private fun OutflowStatCard(
    uiState: TarazUiState,
    isCompact: Boolean,
    modifier: Modifier = Modifier,
) {
    StatSummaryCard(
        title = "هزینه ${uiState.currentShamsiDate.monthName}",
        valueToman = uiState.monthlyOutflowToman,
        currencyUnit = uiState.currencyUnit,
        usePersianDigits = uiState.usePersianDigits,
        icon = Icons.Rounded.ShoppingBag,
        iconTint = TarazTheme.colors.outflow,
        trendPercent = uiState.outflowTrendPercent,
        isPositiveTrendGood = false,
        isCompact = isCompact,
        modifier = modifier,
    )
}

@Composable
private fun SavingsStatCard(
    uiState: TarazUiState,
    isCompact: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    StatSummaryCard(
        title = "تراز خالص ماه",
        valueToman = uiState.monthlyNetToman,
        currencyUnit = uiState.currencyUnit,
        usePersianDigits = uiState.usePersianDigits,
        icon = Icons.Rounded.Savings,
        iconTint = Color(0xFF6366F1),
        trendPercent = uiState.savingsRatePercent,
        isPositiveTrendGood = true,
        accentColor = if (uiState.monthlyNetToman >= 0) colors.navyAnchor else colors.outflow,
        isCompact = isCompact,
        modifier = modifier,
    )
}

@Composable
private fun CashFlowCard(uiState: TarazUiState, modifier: Modifier = Modifier) {
    val colors = TarazTheme.colors
    TarazCard(modifier = modifier) {
        Text(
            text = "جریان نقدی ۶ ماه اخیر",
            style = MaterialTheme.typography.headlineSmall,
            color = colors.textPrimary,
        )
        Text(
            text = "روی هر ستون بزنید تا جزئیات درآمد و هزینه آن ماه نمایش داده شود",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )
        Spacer(modifier = Modifier.height(14.dp))
        CashFlowBarChart(
            points = uiState.cashFlowPoints,
            currencyUnit = uiState.currencyUnit,
            usePersianDigits = uiState.usePersianDigits,
        )
    }
}

@Composable
private fun BudgetSnapshotCard(
    uiState: TarazUiState,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    TarazCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "وضعیت بودجه ماه جاری",
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onViewAll) {
                Text(
                    text = "همه بودجه‌ها",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.techBlue,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val topBudgets = uiState.budgets.take(4)
        if (topBudgets.isEmpty()) {
            Text(
                text = "سقف بودجه‌ای تعریف نشده است.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                topBudgets.forEach { item ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = item.budget.category,
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = PersianFormatUtils.formatPercent(
                                    value = (item.ratio * 100.0),
                                    usePersianDigits = uiState.usePersianDigits,
                                ),
                                style = TabularNumTextStyle.copy(
                                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = when {
                                    item.ratio <= 0.75f -> colors.inflowText
                                    item.ratio <= 0.90f -> colors.warningText
                                    else -> colors.outflowText
                                },
                            )
                        }
                        RtlBudgetProgressBar(ratio = item.ratio)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "مصرف: ${
                                    PersianFormatUtils.formatCurrency(
                                        item.spentToman,
                                        uiState.currencyUnit,
                                        uiState.usePersianDigits,
                                    )
                                }",
                                style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                                color = colors.textMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "سقف: ${
                                    PersianFormatUtils.formatCurrency(
                                        item.budget.monthlyLimitToman,
                                        uiState.currencyUnit,
                                        uiState.usePersianDigits,
                                        includeUnit = true,
                                    )
                                }",
                                style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                                color = colors.textMuted,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentTransactionRow(
    tx: TransactionEntity,
    account: AccountEntity?,
    isCompact: Boolean,
    uiState: TarazUiState,
    onClick: () -> Unit,
) {
    val colors = TarazTheme.colors
    val amountPrefix = if (tx.isIncome) "+" else "-"
    val amountColor = when {
        tx.isIncome -> colors.inflowText
        tx.status == TransactionStatus.OVERDUE.name -> colors.outflow
        else -> colors.textPrimary
    }
    val formattedAmount = "$amountPrefix${
        PersianFormatUtils.formatCurrency(
            tx.amountToman,
            uiState.currencyUnit,
            uiState.usePersianDigits,
        )
    }"

    if (isCompact) {
        // Spacious 2-line responsive mobile card row — every word and digit 100% readable!
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
                    CategoryGlassIconBadge(
                        category = tx.category,
                        isIncome = tx.isIncome,
                        size = 38.dp,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tx.title,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.textPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${tx.category} • ${tx.shamsiDateText}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formattedAmount,
                        style = TabularNumTextStyle.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = amountColor,
                        maxLines = 1,
                    )
                    Text(
                        text = uiState.currencyUnit.labelFa,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = colors.textMuted,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CardSourcePill(
                    account = account,
                    isIncome = tx.isIncome,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(statusName = tx.status)
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(2.2f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                CategoryGlassIconBadge(
                    category = tx.category,
                    isIncome = tx.isIncome,
                    size = 36.dp,
                )
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = tx.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    CardSourcePill(account = account, isIncome = tx.isIncome)
                }
            }
            Box(modifier = Modifier.weight(1.2f)) {
                NeutralTagBadge(text = tx.category)
            }
            Text(
                text = tx.shamsiDateText,
                style = TabularNumTextStyle.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize),
                color = colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            Box(modifier = Modifier.weight(1f)) {
                StatusBadge(statusName = tx.status)
            }
            Text(
                text = formattedAmount,
                style = TabularNumTextStyle.copy(fontWeight = FontWeight.SemiBold),
                color = amountColor,
            )
        }
    }
}
