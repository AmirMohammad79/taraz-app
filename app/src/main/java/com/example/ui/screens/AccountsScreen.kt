package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FullBankCardWidget
import com.example.ui.components.SoftGlassIconBadge
import com.example.ui.components.TarazCard
import com.example.ui.theme.TabularNumTextStyle
import com.example.ui.theme.TarazTheme
import com.example.ui.viewmodel.AccountSummaryUi
import com.example.ui.viewmodel.TarazUiState
import com.example.util.CurrencyUnit
import com.example.util.PersianFormatUtils

@Composable
fun AccountsScreen(
    uiState: TarazUiState,
    isExpanded: Boolean,
    isCompact: Boolean,
    onAddAccount: () -> Unit,
    onEditAccount: (AccountSummaryUi) -> Unit,
    onDeleteAccount: (Long) -> Unit,
    onExpenseFromCard: (Long) -> Unit,
    onIncomeToCard: (Long) -> Unit,
    onTransferFromCard: (Long?) -> Unit,
    onViewCardLedger: (Long) -> Unit,
    onReconcileAccount: (Long) -> Unit,
    onSetCurrencyUnit: (CurrencyUnit) -> Unit,
    onTogglePersianDigits: () -> Unit,
    onRestoreSampleData: () -> Unit,
    onClearAllData: () -> Unit,
) {
    val colors = TarazTheme.colors
    val horizontalPad = if (isCompact) 14.dp else 24.dp

    var pendingDeleteCard by remember { mutableStateOf<AccountSummaryUi?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }

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
        // Total Cards Balance & Quick Card Actions Header
        item {
            TarazCard(
                modifier = Modifier.fillMaxWidth(),
                accentGlow = colors.techBlue,
            ) {
                Text(
                    text = "کارت‌های بانکی و کیف پول‌ها",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textPrimary,
                )
                Text(
                    text = "کارت‌های خود را اضافه کنید و موجودی هر کارت را به‌دلخواه تنظیم نمایید",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "مجموع موجودی ${
                                PersianFormatUtils.formatNumber(
                                    uiState.accountSummaries.size.toLong(),
                                    uiState.usePersianDigits,
                                )
                            } کارت فعال",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textMuted,
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = PersianFormatUtils.formatCurrency(
                                    uiState.totalBalanceToman,
                                    uiState.currencyUnit,
                                    uiState.usePersianDigits,
                                ),
                                style = MaterialTheme.typography.headlineLarge.merge(
                                    TabularNumTextStyle.copy(
                                        fontSize = if (isCompact) 21.sp else 26.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                ),
                                color = colors.navyAnchor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = uiState.currencyUnit.labelFa,
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.textMuted,
                                modifier = Modifier.padding(bottom = 3.dp),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Button(
                        onClick = onAddAccount,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.techBlue,
                            contentColor = Color.White,
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_account_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "افزودن کارت جدید",
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    OutlinedButton(
                        onClick = { onTransferFromCard(null) },
                        enabled = uiState.allAccountSummaries.size >= 2,
                        border = BorderStroke(1.dp, colors.techBlue.copy(alpha = 0.55f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.techBlue),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_transfer_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SwapHoriz,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "کارت به کارت",
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        if (uiState.accountSummaries.isEmpty()) {
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
                            icon = Icons.Rounded.CreditCard,
                            tint = colors.techBlue,
                            size = 56.dp,
                            iconSize = 28.dp,
                            cornerRadius = 16.dp,
                        )
                        Text(
                            text = "هنوز کارتی ثبت نکرده‌اید",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary,
                        )
                        Text(
                            text = "کارت‌های بانکی یا کیف پول نقدی خود را همراه با موجودی فعلی اضافه کنید تا تمام هزینه‌ها و درآمدها به آن متصل شوند.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted,
                        )
                        Button(
                            onClick = onAddAccount,
                            colors = ButtonDefaults.buttonColors(containerColor = colors.techBlue),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ثبت اولین کارت بانکی", color = Color.White)
                        }
                    }
                }
            }
        } else if (isExpanded) {
            val rows = uiState.accountSummaries.chunked(2)
            items(rows) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    FullBankCardWidget(
                        summary = pair[0],
                        currencyUnit = uiState.currencyUnit,
                        usePersianDigits = uiState.usePersianDigits,
                        onExpenseFromCard = { onExpenseFromCard(pair[0].account.id) },
                        onIncomeToCard = { onIncomeToCard(pair[0].account.id) },
                        onTransferFromCard = { onTransferFromCard(pair[0].account.id) },
                        onViewCardLedger = { onViewCardLedger(pair[0].account.id) },
                        onEditCard = { onEditAccount(pair[0]) },
                        onDeleteCard = { pendingDeleteCard = pair[0] },
                        onReconcile = { onReconcileAccount(pair[0].account.id) },
                        modifier = Modifier.weight(1f),
                    )
                    if (pair.size > 1) {
                        FullBankCardWidget(
                            summary = pair[1],
                            currencyUnit = uiState.currencyUnit,
                            usePersianDigits = uiState.usePersianDigits,
                            onExpenseFromCard = { onExpenseFromCard(pair[1].account.id) },
                            onIncomeToCard = { onIncomeToCard(pair[1].account.id) },
                            onTransferFromCard = { onTransferFromCard(pair[1].account.id) },
                            onViewCardLedger = { onViewCardLedger(pair[1].account.id) },
                            onEditCard = { onEditAccount(pair[1]) },
                            onDeleteCard = { pendingDeleteCard = pair[1] },
                            onReconcile = { onReconcileAccount(pair[1].account.id) },
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        } else {
            items(uiState.accountSummaries, key = { it.account.id }) { summary ->
                FullBankCardWidget(
                    summary = summary,
                    currencyUnit = uiState.currencyUnit,
                    usePersianDigits = uiState.usePersianDigits,
                    onExpenseFromCard = { onExpenseFromCard(summary.account.id) },
                    onIncomeToCard = { onIncomeToCard(summary.account.id) },
                    onTransferFromCard = { onTransferFromCard(summary.account.id) },
                    onViewCardLedger = { onViewCardLedger(summary.account.id) },
                    onEditCard = { onEditAccount(summary) },
                    onDeleteCard = { pendingDeleteCard = summary },
                    onReconcile = { onReconcileAccount(summary.account.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // Display & Real-Use Data Preferences
        item {
            TarazCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "تنظیمات و مدیریت داده‌ها",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.textPrimary,
                )
                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "واحد پولی نمایشی",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = "نمایش خودکار تمام مبالغ کارت‌ها و تراکنش‌ها به تومان یا ریال",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        CurrencyUnit.entries.forEach { unit ->
                            val selected = uiState.currencyUnit == unit
                            Button(
                                onClick = { onSetCurrencyUnit(unit) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) colors.techBlue else colors.glassInnerSheen,
                                    contentColor = if (selected) Color.White else colors.textPrimary,
                                ),
                                border = if (selected) {
                                    BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                                } else {
                                    BorderStroke(1.dp, colors.glassBorderHighlight)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(unit.labelFa, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = colors.border, thickness = 1.dp)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ارقام فارسی در جداول مالی",
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.textPrimary,
                        )
                        Text(
                            text = "نمایش اعداد با ارقام فارسی (۰ تا ۹)",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = uiState.usePersianDigits,
                        onCheckedChange = { onTogglePersianDigits() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = colors.techBlue,
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = colors.border, thickness = 1.dp)
                Spacer(modifier = Modifier.height(14.dp))

                // Responsive data management buttons (full-width stacked on compact so labels NEVER clip)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { showClearConfirm = true },
                        border = BorderStroke(1.dp, colors.outflow.copy(alpha = 0.55f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.outflow),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("clear_all_data_btn"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("شروع از صفر (پاکسازی داده‌های نمونه)", style = MaterialTheme.typography.labelLarge)
                    }

                    OutlinedButton(
                        onClick = onRestoreSampleData,
                        border = BorderStroke(1.dp, colors.border),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("بازیابی داده‌های نمونه برای تست", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }

    pendingDeleteCard?.let { cardToDelete ->
        AlertDialog(
            onDismissRequest = { pendingDeleteCard = null },
            title = {
                Text(
                    text = "حذف «${cardToDelete.account.name}»؟",
                    style = MaterialTheme.typography.headlineSmall,
                )
            },
            text = {
                Text(
                    text = "با حذف این کارت، تمامی تراکنش‌های ثبت‌شده برای این کارت نیز حذف خواهند شد. آیا مطمئن هستید؟",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAccount(cardToDelete.account.id)
                        pendingDeleteCard = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.outflow),
                ) {
                    Text("بله، حذف شود", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteCard = null }) {
                    Text("انصراف")
                }
            },
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = {
                Text(
                    text = "پاکسازی همه داده‌ها؟",
                    style = MaterialTheme.typography.headlineSmall,
                )
            },
            text = {
                Text(
                    text = "تمامی کارت‌ها، تراکنش‌ها و بودجه‌های فعلی پاک می‌شوند تا بتوانید با کارت‌ها و موجودی‌های واقعی خود شروع کنید.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.outflow),
                ) {
                    Text("پاکسازی و شروع واقعی", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("انصراف")
                }
            },
        )
    }
}
