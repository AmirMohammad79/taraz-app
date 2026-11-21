package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.AccountEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionStatus
import com.example.ui.components.CardSourcePill
import com.example.ui.components.CategoryGlassIconBadge
import com.example.ui.components.NeutralTagBadge
import com.example.ui.components.SoftGlassIconBadge
import com.example.ui.components.StatusBadge
import com.example.ui.components.TarazCard
import com.example.ui.components.cardPaletteFor
import com.example.ui.components.glassPanel
import com.example.ui.theme.TabularNumTextStyle
import com.example.ui.theme.TarazTheme
import com.example.ui.viewmodel.LedgerFilter
import com.example.ui.viewmodel.TarazUiState
import com.example.util.PersianFormatUtils

@Composable
fun LedgerScreen(
    uiState: TarazUiState,
    isCompact: Boolean,
    onSearchChange: (String) -> Unit,
    onSelectFilter: (LedgerFilter) -> Unit,
    onSelectCardFilter: (Long?) -> Unit,
    onToggleSelection: (Long) -> Unit,
    onClearSelection: () -> Unit,
    onVerifySelected: () -> Unit,
    onDeleteSelected: () -> Unit,
    onEditTransaction: (TransactionEntity) -> Unit,
    onToggleStatus: (TransactionEntity) -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    onOpenNewTransaction: () -> Unit,
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
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "دفتر کل تراکنش‌ها",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = "نمایش ${
                            PersianFormatUtils.formatNumber(
                                uiState.filteredTransactions.size.toLong(),
                                uiState.usePersianDigits,
                            )
                        } تراکنش ثبت‌شده",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                    )
                }
            }
        }

        // Search, Status Filters, and Bank Card Filter Bar
        item {
            TarazCard(contentPadding = PaddingValues(14.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = {
                            Text(
                                text = "جستجو در شرح، دسته، کارت یا تاریخ...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = stringResource(R.string.cd_search_transactions),
                                tint = colors.textMuted,
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "پاک کردن جستجو",
                                        tint = colors.textMuted,
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.techBlue,
                            unfocusedBorderColor = colors.glassBorderHighlight,
                            focusedContainerColor = colors.glassSurfaceTop,
                            unfocusedContainerColor = colors.glassInnerSheen,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ledger_search_input"),
                    )

                    // Transaction Type & Status Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        LedgerFilter.entries.forEach { filter ->
                            val selected = filter == uiState.ledgerFilter
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (selected) colors.activeNavBg else colors.glassInnerSheen,
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) colors.techBlue else colors.glassBorderHighlight,
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable { onSelectFilter(filter) }
                                    .testTag("ledger_filter_${filter.name}"),
                            ) {
                                Text(
                                    text = filter.labelFa,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    ),
                                    color = if (selected) colors.techBlue else colors.textMuted,
                                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
                                )
                            }
                        }
                    }

                    // Bank Card Filter Row
                    if (uiState.accounts.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val allCardsSelected = uiState.selectedCardFilterId == null
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (allCardsSelected) colors.techBlue.copy(alpha = 0.14f) else colors.glassInnerSheen,
                                border = BorderStroke(
                                    1.dp,
                                    if (allCardsSelected) colors.techBlue else colors.glassBorderHighlight,
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable { onSelectCardFilter(null) }
                                    .testTag("card_filter_ALL"),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CreditCard,
                                        contentDescription = null,
                                        tint = if (allCardsSelected) colors.techBlue else colors.textMuted,
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Text(
                                        text = "همه کارت‌ها",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (allCardsSelected) FontWeight.Bold else FontWeight.Medium,
                                        ),
                                        color = if (allCardsSelected) colors.techBlue else colors.textMuted,
                                    )
                                }
                            }

                            uiState.accounts.forEach { acc ->
                                val selected = uiState.selectedCardFilterId == acc.id
                                val palette = cardPaletteFor(acc.cardColorIndex)
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (selected) palette.accent.copy(alpha = 0.16f) else colors.glassInnerSheen,
                                    border = BorderStroke(
                                        1.dp,
                                        if (selected) palette.accent else colors.glassBorderHighlight,
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .clickable { onSelectCardFilter(if (selected) null else acc.id) }
                                        .testTag("card_filter_${acc.id}"),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(palette.accent),
                                        )
                                        Text(
                                            text = acc.name,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            ),
                                            color = if (selected) palette.accent else colors.textPrimary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bulk selection bar (responsive wrap for compact screens)
        item {
            AnimatedVisibility(visible = uiState.selectedTransactionIds.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = colors.activeNavBg,
                    border = BorderStroke(1.dp, colors.techBlue.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${
                                    PersianFormatUtils.formatNumber(
                                        uiState.selectedTransactionIds.size.toLong(),
                                        uiState.usePersianDigits,
                                    )
                                } ردیف انتخاب شده",
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.techBlue,
                            )
                            TextButton(onClick = onClearSelection) {
                                Text("انصراف", color = colors.textMuted)
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Button(
                                onClick = onVerifySelected,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.inflow,
                                    contentColor = Color.White,
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("تایید انتخاب‌ها", style = MaterialTheme.typography.labelMedium)
                            }
                            OutlinedButton(
                                onClick = onDeleteSelected,
                                border = BorderStroke(1.dp, colors.outflow),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.outflow),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("حذف انتخاب‌ها", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }

        // Desktop/Tablet Table Header OR Empty State
        if (!isCompact || uiState.filteredTransactions.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassPanel(shape = RoundedCornerShape(16.dp), elevation = 4.dp),
                ) {
                    if (!isCompact) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.tableHeaderSurface)
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "شرح و کارت بانکی",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.textMuted,
                                modifier = Modifier.weight(2.3f),
                            )
                            Text(
                                text = "دسته",
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
                                modifier = Modifier.weight(1.05f),
                            )
                            Text(
                                text = "مبلغ (${uiState.currencyUnit.labelFa})",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.textMuted,
                                modifier = Modifier.weight(1.2f),
                            )
                            Spacer(modifier = Modifier.width(72.dp))
                        }
                        HorizontalDivider(thickness = 1.dp, color = colors.border)
                    }

                    if (uiState.filteredTransactions.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            SoftGlassIconBadge(
                                icon = Icons.Rounded.ReceiptLong,
                                tint = colors.techBlue,
                                size = 52.dp,
                                iconSize = 26.dp,
                                cornerRadius = 16.dp,
                            )
                            Text(
                                text = "تراکنشی با این فیلتر یافت نشد",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textPrimary,
                            )
                            Text(
                                text = "فیلترها را پاک کنید یا یک هزینه یا درآمد جدید ثبت نمایید.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textMuted,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (uiState.searchQuery.isNotEmpty() || uiState.ledgerFilter != LedgerFilter.ALL || uiState.selectedCardFilterId != null) {
                                    OutlinedButton(
                                        onClick = {
                                            onSearchChange("")
                                            onSelectFilter(LedgerFilter.ALL)
                                            onSelectCardFilter(null)
                                        },
                                    ) {
                                        Text("پاک کردن فیلترها")
                                    }
                                }
                                Button(
                                    onClick = onOpenNewTransaction,
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.techBlue),
                                ) {
                                    Text("ثبت تراکنش جدید", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (uiState.filteredTransactions.isNotEmpty()) {
            itemsIndexed(
                items = uiState.filteredTransactions,
                key = { _, tx -> tx.id },
            ) { _, tx ->
                val account = accountMap[tx.accountId]
                val isSelected = uiState.selectedTransactionIds.contains(tx.id)
                LedgerTableRow(
                    tx = tx,
                    account = account,
                    isCompact = isCompact,
                    isSelected = isSelected,
                    uiState = uiState,
                    onToggleSelect = { onToggleSelection(tx.id) },
                    onEdit = { onEditTransaction(tx) },
                    onToggleStatus = { onToggleStatus(tx) },
                    onDelete = { onDeleteTransaction(tx.id) },
                )
            }
        }
    }
}

@Composable
private fun LedgerTableRow(
    tx: TransactionEntity,
    account: AccountEntity?,
    isCompact: Boolean,
    isSelected: Boolean,
    uiState: TarazUiState,
    onToggleSelect: () -> Unit,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
) {
    val colors = TarazTheme.colors
    var showMenu by remember { mutableStateOf(false) }

    val prefix = if (tx.isIncome) "+" else "-"
    val amountColor = when {
        tx.isIncome -> colors.inflowText
        tx.status == TransactionStatus.OVERDUE.name -> colors.outflow
        else -> colors.textPrimary
    }
    val formattedAmount = "$prefix${
        PersianFormatUtils.formatCurrency(
            tx.amountToman,
            uiState.currencyUnit,
            uiState.usePersianDigits,
        )
    }"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .glassPanel(
                shape = RoundedCornerShape(16.dp),
                topColor = if (isSelected) colors.activeNavBg else colors.glassSurfaceTop,
                bottomColor = if (isSelected) colors.activeNavBg.copy(alpha = 0.7f) else colors.glassSurfaceBottom,
                borderHighlight = if (isSelected) colors.techBlue else colors.glassBorderHighlight,
                elevation = 4.dp,
            )
            .clickable(onClick = onEdit),
    ) {
        if (isCompact) {
            // Spacious mobile-first 2-row card — zero crushed text!
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
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
                                fontSize = 15.sp,
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

                HorizontalDivider(thickness = 1.dp, color = colors.border.copy(alpha = 0.6f))

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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        StatusBadge(statusName = tx.status)
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onToggleSelect() },
                            colors = CheckboxDefaults.colors(
                                checkedColor = colors.techBlue,
                                uncheckedColor = colors.textMuted,
                            ),
                            modifier = Modifier.size(36.dp),
                        )
                        LedgerRowMenu(
                            tx = tx,
                            expanded = showMenu,
                            onToggleExpand = { showMenu = it },
                            onEdit = onEdit,
                            onToggleStatus = onToggleStatus,
                            onDelete = onDelete,
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(2.3f),
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
                Box(modifier = Modifier.weight(1.05f)) {
                    StatusBadge(statusName = tx.status)
                }
                Text(
                    text = formattedAmount,
                    style = TabularNumTextStyle.copy(fontWeight = FontWeight.SemiBold),
                    color = amountColor,
                    modifier = Modifier.weight(1.2f),
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleSelect() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colors.techBlue,
                            uncheckedColor = colors.textMuted,
                        ),
                        modifier = Modifier.minimumInteractiveComponentSize(),
                    )
                    LedgerRowMenu(
                        tx = tx,
                        expanded = showMenu,
                        onToggleExpand = { showMenu = it },
                        onEdit = onEdit,
                        onToggleStatus = onToggleStatus,
                        onDelete = onDelete,
                    )
                }
            }
        }
    }
}

@Composable
private fun LedgerRowMenu(
    tx: TransactionEntity,
    expanded: Boolean,
    onToggleExpand: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
) {
    val colors = TarazTheme.colors
    Box {
        IconButton(
            onClick = { onToggleExpand(true) },
            modifier = Modifier.size(36.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = stringResource(R.string.cd_more_actions),
                tint = colors.textMuted,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onToggleExpand(false) },
        ) {
            DropdownMenuItem(
                text = { Text("ویرایش تراکنش") },
                leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null) },
                onClick = {
                    onToggleExpand(false)
                    onEdit()
                },
            )
            DropdownMenuItem(
                text = {
                    Text(
                        if (tx.status == TransactionStatus.VERIFIED.name) "تغییر به در انتظار"
                        else "تایید سند"
                    )
                },
                leadingIcon = { Icon(Icons.Rounded.CheckCircleOutline, contentDescription = null) },
                onClick = {
                    onToggleExpand(false)
                    onToggleStatus()
                },
            )
            DropdownMenuItem(
                text = { Text("حذف تراکنش", color = colors.outflow) },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.DeleteOutline,
                        contentDescription = null,
                        tint = colors.outflow,
                    )
                },
                onClick = {
                    onToggleExpand(false)
                    onDelete()
                },
            )
        }
    }
}
