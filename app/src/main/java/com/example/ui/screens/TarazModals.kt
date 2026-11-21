package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.local.AccountEntity
import com.example.data.local.AccountKind
import com.example.data.local.BudgetEntity
import com.example.data.local.BudgetIconKey
import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionStatus
import com.example.data.local.WorkspaceScope
import com.example.ui.components.TarazCardPalettes
import com.example.ui.components.cardPaletteFor
import com.example.ui.components.glassPanel
import com.example.ui.theme.TabularNumTextStyle
import com.example.ui.theme.TarazTheme
import com.example.ui.viewmodel.AccountSummaryUi
import com.example.util.CurrencyUnit
import com.example.util.PersianFormatUtils
import com.example.util.ShamsiDateUtils

private val IRANIAN_BANK_PRESETS = listOf(
    "بانک ملت",
    "بانک ملی",
    "بانک سامان",
    "بانک پاسارگاد",
    "بلوبانک",
    "بانک صادرات",
    "بانک تجارت",
    "بانک رفاه",
    "صندوق نقدی",
)

@Composable
fun TransactionModalDialog(
    initial: TransactionEntity?,
    preselectedIsIncome: Boolean?,
    preselectedAccountId: Long?,
    accountSummaries: List<AccountSummaryUi>,
    currencyUnit: CurrencyUnit,
    usePersianDigits: Boolean,
    onDismiss: () -> Unit,
    onSave: (TransactionEntity) -> Unit,
    onDelete: (Long) -> Unit,
    onRequestAddCard: () -> Unit,
) {
    val colors = TarazTheme.colors
    val isEditing = initial != null
    val nowShamsi = remember { ShamsiDateUtils.nowShamsi() }

    var isIncome by remember(initial, preselectedIsIncome) {
        mutableStateOf(initial?.isIncome ?: preselectedIsIncome ?: false)
    }
    var title by remember(initial) { mutableStateOf(initial?.title ?: "") }
    var rawAmountDigits by remember(initial) {
        val scaled = (initial?.amountToman ?: 0L) * currencyUnit.multiplierFromToman
        mutableStateOf(if (scaled > 0L) scaled.toString() else "")
    }
    var category by remember(initial, preselectedIsIncome) {
        mutableStateOf(
            initial?.category ?: if (isIncome) "درآمد اصلی" else "خوراک و خرید"
        )
    }
    var selectedAccountId by remember(initial, preselectedAccountId, accountSummaries) {
        mutableStateOf(
            initial?.accountId
                ?: preselectedAccountId
                ?: accountSummaries.firstOrNull()?.account?.id
                ?: 0L
        )
    }
    var status by remember(initial) {
        mutableStateOf(initial?.status ?: TransactionStatus.VERIFIED.name)
    }
    var workspace by remember(initial, selectedAccountId) {
        val matched = accountSummaries.firstOrNull { it.account.id == selectedAccountId }
        mutableStateOf(initial?.workspace ?: matched?.account?.workspace ?: WorkspaceScope.PERSONAL.name)
    }
    var note by remember(initial) { mutableStateOf(initial?.note ?: "") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val categories = if (isIncome) {
        listOf("درآمد اصلی", "پروژه و مشاوره", "سرمایه‌گذاری", "هدیه و بازگشت وجه", "سایر درآمدها")
    } else {
        listOf("خوراک و خرید", "مسکن و اجاره", "حمل‌ونقل", "نرم‌افزار و اشتراک", "بهداشت و درمان", "سرمایه‌گذاری", "سایر هزینه‌ها")
    }

    val selectedSummary = accountSummaries.firstOrNull { it.account.id == selectedAccountId }
    val enteredToman = remember(rawAmountDigits, currencyUnit) {
        val raw = rawAmountDigits.toLongOrNull() ?: 0L
        if (currencyUnit == CurrencyUnit.RIAL) raw / 10L else raw
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .widthIn(max = 520.dp)
                .glassPanel(
                    shape = RoundedCornerShape(22.dp),
                    topColor = colors.glassModalSurface,
                    bottomColor = colors.glassSurfaceTop,
                    elevation = 16.dp,
                ),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (isEditing) "ویرایش تراکنش" else "ثبت تراکنش جدید",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.textPrimary,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.cd_close_dialog),
                            tint = colors.textMuted,
                        )
                    }
                }

                HorizontalDivider(color = colors.border, thickness = 1.dp)

                // Expense vs Income Segmented Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ModalChoiceButton(
                        label = "هزینه (برداشت)",
                        selected = !isIncome,
                        activeColor = colors.outflow,
                        onClick = {
                            isIncome = false
                            if (category in listOf("درآمد اصلی", "پروژه و مشاوره", "هدیه و بازگشت وجه", "سایر درآمدها")) {
                                category = "خوراک و خرید"
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("choice_expense"),
                    )
                    ModalChoiceButton(
                        label = "درآمد (واریز)",
                        selected = isIncome,
                        activeColor = colors.inflow,
                        onClick = {
                            isIncome = true
                            if (category !in listOf("درآمد اصلی", "پروژه و مشاوره", "سرمایه‌گذاری", "هدیه و بازگشت وجه", "سایر درآمدها")) {
                                category = "درآمد اصلی"
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("choice_income"),
                    )
                }

                // PROMINENT BANK CARD SELECTOR
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (isIncome) "واریز به کدام کارت؟" else "برداشت از کدام کارت؟",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = colors.navyAnchor,
                        )
                        TextButton(
                            onClick = onRequestAddCard,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = colors.techBlue)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("کارت جدید", style = MaterialTheme.typography.labelSmall, color = colors.techBlue)
                        }
                    }

                    if (accountSummaries.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = colors.warningBg,
                            border = BorderStroke(1.dp, colors.warning.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = "ابتدا حداقل یک کارت بانکی یا کیف پول اضافه کنید تا موجودی آن محاسبه شود.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.warningText,
                                )
                                Button(
                                    onClick = onRequestAddCard,
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.techBlue),
                                    shape = RoundedCornerShape(10.dp),
                                ) {
                                    Text("افزودن اولین کارت", color = Color.White)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            accountSummaries.forEach { summary ->
                                val acc = summary.account
                                val selected = selectedAccountId == acc.id
                                val palette = cardPaletteFor(acc.cardColorIndex)
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (selected) palette.accent.copy(alpha = 0.14f) else colors.glassInnerSheen,
                                    border = BorderStroke(
                                        width = if (selected) 1.8.dp else 1.dp,
                                        color = if (selected) palette.accent else colors.glassBorderHighlight,
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            selectedAccountId = acc.id
                                            workspace = acc.workspace
                                            validationError = null
                                        }
                                        .testTag("tx_card_option_${acc.id}"),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                        verticalArrangement = Arrangement.spacedBy(2.dp),
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(9.dp)
                                                    .clip(CircleShape)
                                                    .background(palette.accent),
                                            )
                                            Text(
                                                text = acc.name,
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = if (selected) palette.accent else colors.textPrimary,
                                                maxLines = 1,
                                            )
                                        }
                                        Text(
                                            text = "موجودی: ${
                                                PersianFormatUtils.formatCurrency(
                                                    summary.currentBalanceToman,
                                                    currencyUnit,
                                                    usePersianDigits,
                                                    includeUnit = true,
                                                )
                                            }",
                                            style = TabularNumTextStyle.copy(fontSize = 11.sp),
                                            color = colors.textMuted,
                                            maxLines = 1,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Title input
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        validationError = null
                    },
                    label = { Text("شرح تراکنش") },
                    placeholder = { Text(if (isIncome) "مثلاً: واریز حقوق یا تسویه پروژه" else "مثلاً: خرید سوپرمارکت یا قبض") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.techBlue,
                        unfocusedBorderColor = colors.border,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tx_title_input"),
                )

                // Amount Input
                OutlinedTextField(
                    value = PersianFormatUtils.formatInputWithCommas(rawAmountDigits, usePersianDigits),
                    onValueChange = { input ->
                        rawAmountDigits = PersianFormatUtils.toAsciiDigits(input).take(13)
                        validationError = null
                    },
                    label = { Text("مبلغ") },
                    placeholder = { Text("۰") },
                    trailingIcon = {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = colors.glassInnerSheen,
                            border = BorderStroke(1.dp, colors.border),
                            modifier = Modifier.padding(end = 8.dp),
                        ) {
                            Text(
                                text = currencyUnit.labelFa,
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.textMuted,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            )
                        }
                    },
                    textStyle = TabularNumTextStyle.copy(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.techBlue,
                        unfocusedBorderColor = colors.border,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tx_amount_input"),
                )

                // Live balance impact preview for the selected card!
                if (selectedSummary != null && enteredToman > 0L && !isEditing) {
                    val projected = if (isIncome) {
                        selectedSummary.currentBalanceToman + enteredToman
                    } else {
                        selectedSummary.currentBalanceToman - enteredToman
                    }
                    val isNegativeProjected = projected < 0L
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isNegativeProjected) colors.outflowBg else colors.activeNavBg)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (isNegativeProjected) {
                            Icon(
                                imageVector = Icons.Rounded.WarningAmber,
                                contentDescription = null,
                                tint = colors.outflow,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        Text(
                            text = "موجودی «${selectedSummary.account.name}» پس از ثبت: ${
                                PersianFormatUtils.formatCurrency(
                                    projected,
                                    currencyUnit,
                                    usePersianDigits,
                                    includeUnit = true,
                                )
                            }",
                            style = TabularNumTextStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                            color = if (isNegativeProjected) colors.outflowText else colors.navyAnchor,
                        )
                    }
                }

                // Category Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "دسته‌بندی",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textMuted,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        categories.forEach { cat ->
                            ModalFilterChip(
                                label = cat,
                                selected = category == cat,
                                onClick = { category = cat },
                            )
                        }
                    }
                }

                // Status selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "وضعیت سند",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textMuted,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        TransactionStatus.entries.forEach { st ->
                            ModalFilterChip(
                                label = st.labelFa,
                                selected = status == st.name,
                                onClick = { status = st.name },
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("یادداشت اختیاری") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.techBlue,
                        unfocusedBorderColor = colors.border,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                if (validationError != null) {
                    Text(
                        text = validationError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.outflow,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (isEditing && initial != null) {
                        OutlinedButton(
                            onClick = { onDelete(initial.id) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, colors.outflow.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.outflow),
                        ) {
                            Icon(Icons.Rounded.DeleteOutline, contentDescription = "حذف", modifier = Modifier.size(18.dp))
                        }
                    } else {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, colors.border),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("انصراف", color = colors.textMuted)
                        }
                    }

                    Button(
                        onClick = {
                            val cleanTitle = title.trim()
                            when {
                                accountSummaries.isEmpty() || selectedSummary == null ->
                                    validationError = "لطفاً ابتدا کارت بانکی را انتخاب یا اضافه کنید."
                                cleanTitle.isEmpty() ->
                                    validationError = "لطفاً شرح تراکنش را وارد کنید."
                                enteredToman <= 0L ->
                                    validationError = "لطفاً مبلغ معتبر بزرگ‌تر از صفر وارد کنید."
                                else -> {
                                    onSave(
                                        TransactionEntity(
                                            id = initial?.id ?: 0L,
                                            title = cleanTitle,
                                            amountToman = enteredToman,
                                            isIncome = isIncome,
                                            category = category,
                                            accountId = selectedSummary.account.id,
                                            workspace = workspace,
                                            status = status,
                                            shamsiDateText = initial?.shamsiDateText
                                                ?: nowShamsi.toFormattedSlash(usePersianDigits = true),
                                            shamsiYear = initial?.shamsiYear ?: nowShamsi.year,
                                            shamsiMonth = initial?.shamsiMonth ?: nowShamsi.month,
                                            timestampMs = initial?.timestampMs ?: System.currentTimeMillis(),
                                            note = note.trim(),
                                        )
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.techBlue,
                            contentColor = Color.White,
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("save_transaction_button"),
                    ) {
                        Text(if (isEditing) "ذخیره تغییرات" else "ثبت تراکنش")
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetModalDialog(
    initial: BudgetEntity?,
    currencyUnit: CurrencyUnit,
    usePersianDigits: Boolean,
    onDismiss: () -> Unit,
    onSave: (BudgetEntity) -> Unit,
) {
    val colors = TarazTheme.colors
    var category by remember(initial) { mutableStateOf(initial?.category ?: "خوراک و خرید") }
    var rawLimitDigits by remember(initial) {
        val scaled = (initial?.monthlyLimitToman ?: 0L) * currencyUnit.multiplierFromToman
        mutableStateOf(if (scaled > 0L) scaled.toString() else "")
    }
    var iconKey by remember(initial) { mutableStateOf(initial?.iconKey ?: BudgetIconKey.FOOD.name) }
    var workspace by remember(initial) { mutableStateOf(initial?.workspace ?: WorkspaceScope.PERSONAL.name) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .widthIn(max = 460.dp)
                .glassPanel(
                    shape = RoundedCornerShape(22.dp),
                    topColor = colors.glassModalSurface,
                    bottomColor = colors.glassSurfaceTop,
                    elevation = 16.dp,
                ),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (initial != null) "ویرایش سقف بودجه" else "تعریف بودجه جدید",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.textPrimary,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.cd_close_dialog))
                    }
                }

                OutlinedTextField(
                    value = category,
                    onValueChange = {
                        category = it
                        errorMsg = null
                    },
                    label = { Text("عنوان دسته‌بندی") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = PersianFormatUtils.formatInputWithCommas(rawLimitDigits, usePersianDigits),
                    onValueChange = {
                        rawLimitDigits = PersianFormatUtils.toAsciiDigits(it).take(13)
                        errorMsg = null
                    },
                    label = { Text("سقف بودجه ماهانه") },
                    trailingIcon = {
                        Text(
                            text = currencyUnit.labelFa,
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textMuted,
                            modifier = Modifier.padding(end = 12.dp),
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    BudgetIconKey.entries.forEach { key ->
                        ModalFilterChip(
                            label = key.labelFa,
                            selected = iconKey == key.name,
                            onClick = {
                                iconKey = key.name
                                if (initial == null) category = key.labelFa
                            },
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModalFilterChip(
                        label = "شخصی",
                        selected = workspace == WorkspaceScope.PERSONAL.name,
                        onClick = { workspace = WorkspaceScope.PERSONAL.name },
                    )
                    ModalFilterChip(
                        label = "کسب‌وکار",
                        selected = workspace == WorkspaceScope.BUSINESS.name,
                        onClick = { workspace = WorkspaceScope.BUSINESS.name },
                    )
                }

                if (errorMsg != null) {
                    Text(errorMsg!!, style = MaterialTheme.typography.bodySmall, color = colors.outflow)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("انصراف", color = colors.textMuted)
                    }
                    Button(
                        onClick = {
                            val cleanCat = category.trim()
                            val raw = rawLimitDigits.toLongOrNull() ?: 0L
                            val limitToman = if (currencyUnit == CurrencyUnit.RIAL) raw / 10L else raw
                            when {
                                cleanCat.isEmpty() -> errorMsg = "عنوان دسته‌بندی الزامی است."
                                limitToman <= 0L -> errorMsg = "سقف بودجه باید بیشتر از صفر باشد."
                                else -> onSave(
                                    BudgetEntity(
                                        id = initial?.id ?: 0L,
                                        category = cleanCat,
                                        monthlyLimitToman = limitToman,
                                        workspace = workspace,
                                        iconKey = iconKey,
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.techBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("ذخیره", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AccountModalDialog(
    initial: AccountEntity?,
    existingTransactionDeltaToman: Long,
    currencyUnit: CurrencyUnit,
    usePersianDigits: Boolean,
    onDismiss: () -> Unit,
    onSave: (AccountEntity) -> Unit,
) {
    val colors = TarazTheme.colors
    val isEditing = initial != null
    val initialLiveBalanceToman = if (initial != null) initial.initialBalanceToman + existingTransactionDeltaToman else 0L

    var name by remember(initial) { mutableStateOf(initial?.name ?: "") }
    var bankName by remember(initial) { mutableStateOf(initial?.bankName ?: "بانک ملت") }
    var rawCardDigits by remember(initial) {
        mutableStateOf(PersianFormatUtils.toAsciiDigits(initial?.cardNumber ?: "").take(16))
    }
    var rawBalanceDigits by remember(initial, existingTransactionDeltaToman) {
        val scaled = initialLiveBalanceToman.coerceAtLeast(0L) * currencyUnit.multiplierFromToman
        mutableStateOf(if (initial != null) scaled.toString() else "")
    }
    var kind by remember(initial) { mutableStateOf(initial?.accountKind ?: AccountKind.CHECKING.name) }
    var workspace by remember(initial) { mutableStateOf(initial?.workspace ?: WorkspaceScope.PERSONAL.name) }
    var colorIndex by remember(initial) { mutableIntStateOf(initial?.cardColorIndex ?: 0) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .widthIn(max = 480.dp)
                .glassPanel(
                    shape = RoundedCornerShape(22.dp),
                    topColor = colors.glassModalSurface,
                    bottomColor = colors.glassSurfaceTop,
                    elevation = 16.dp,
                ),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (isEditing) "ویرایش کارت و موجودی" else "افزودن کارت بانکی جدید",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.textPrimary,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.cd_close_dialog))
                    }
                }

                // Quick Iranian Bank Presets
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "انتخاب سریع بانک",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textMuted,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        IRANIAN_BANK_PRESETS.forEach { preset ->
                            ModalFilterChip(
                                label = preset,
                                selected = bankName == preset,
                                onClick = {
                                    bankName = preset
                                    if (name.isBlank() || name.startsWith("کارت ")) {
                                        name = if (preset == "صندوق نقدی") "کیف پول نقدی" else "کارت ${preset.removePrefix("بانک ")}"
                                    }
                                    if (preset == "صندوق نقدی") {
                                        kind = AccountKind.CASH.name
                                    }
                                },
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMsg = null },
                    label = { Text("نام دلخواه کارت") },
                    placeholder = { Text("مثلاً: کارت حقوق ملت یا کارت روزمره بلو") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_name_input"),
                )

                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it; errorMsg = null },
                    label = { Text("نام بانک یا کیف پول") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )

                // Direct Current Balance Entry
                OutlinedTextField(
                    value = PersianFormatUtils.formatInputWithCommas(rawBalanceDigits, usePersianDigits),
                    onValueChange = { rawBalanceDigits = PersianFormatUtils.toAsciiDigits(it).take(13) },
                    label = { Text("موجودی فعلی کارت") },
                    placeholder = { Text("مثلاً: ۲۵,۰۰۰,۰۰۰") },
                    trailingIcon = {
                        Text(
                            text = currencyUnit.labelFa,
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textMuted,
                            modifier = Modifier.padding(end = 12.dp),
                        )
                    },
                    textStyle = TabularNumTextStyle.copy(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_balance_input"),
                )

                // 16-digit card number input (LTR formatted 4-4-4-4)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    val formattedCard = rawCardDigits.chunked(4).joinToString(" ")
                    OutlinedTextField(
                        value = if (usePersianDigits) PersianFormatUtils.toPersianDigits(formattedCard) else formattedCard,
                        onValueChange = { input ->
                            rawCardDigits = PersianFormatUtils.toAsciiDigits(input).take(16)
                        },
                        label = { Text("شماره کارت ۱۶ رقمی (اختیاری)") },
                        placeholder = { Text("6037 9918 0000 1234") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // Card Color Palette Picker
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "رنگ کارت بانکی",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textMuted,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        TarazCardPalettes.forEachIndexed { idx, palette ->
                            val selected = colorIndex == idx
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(palette.start, palette.end))
                                    )
                                    .border(
                                        width = if (selected) 2.5.dp else 1.dp,
                                        color = if (selected) colors.navyAnchor else Color.White.copy(alpha = 0.8f),
                                        shape = CircleShape,
                                    )
                                    .clickable { colorIndex = idx },
                                contentAlignment = Alignment.Center,
                            ) {
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = palette.nameFa,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AccountKind.entries.forEach { k ->
                        ModalFilterChip(
                            label = k.labelFa,
                            selected = kind == k.name,
                            onClick = { kind = k.name },
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModalFilterChip(
                        label = "شخصی",
                        selected = workspace == WorkspaceScope.PERSONAL.name,
                        onClick = { workspace = WorkspaceScope.PERSONAL.name },
                    )
                    ModalFilterChip(
                        label = "کسب‌وکار",
                        selected = workspace == WorkspaceScope.BUSINESS.name,
                        onClick = { workspace = WorkspaceScope.BUSINESS.name },
                    )
                }

                if (errorMsg != null) {
                    Text(errorMsg!!, style = MaterialTheme.typography.bodySmall, color = colors.outflow)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("انصراف", color = colors.textMuted)
                    }
                    Button(
                        onClick = {
                            val cleanName = name.trim()
                            val cleanBank = bankName.trim().ifEmpty { "بانک" }
                            val raw = rawBalanceDigits.toLongOrNull() ?: 0L
                            val desiredLiveToman = if (currencyUnit == CurrencyUnit.RIAL) raw / 10L else raw
                            val computedInitialToman = if (isEditing) {
                                desiredLiveToman - existingTransactionDeltaToman
                            } else {
                                desiredLiveToman
                            }
                            val formattedCardNum = if (rawCardDigits.isNotEmpty()) {
                                rawCardDigits.chunked(4).joinToString(" ")
                            } else {
                                initial?.cardNumber ?: ""
                            }

                            if (cleanName.isEmpty()) {
                                errorMsg = "لطفاً نام کارت را وارد کنید."
                            } else {
                                onSave(
                                    AccountEntity(
                                        id = initial?.id ?: 0L,
                                        name = cleanName,
                                        bankName = cleanBank,
                                        accountKind = kind,
                                        ibanMasked = initial?.ibanMasked?.ifBlank { formattedCardNum }
                                            ?: formattedCardNum.ifBlank { "کارت بانکی" },
                                        initialBalanceToman = computedInitialToman,
                                        workspace = workspace,
                                        lastReconciledDate = initial?.lastReconciledDate
                                            ?: ShamsiDateUtils.nowShamsi().toFormattedSlash(true),
                                        cardNumber = formattedCardNum,
                                        cardColorIndex = colorIndex,
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.techBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("save_card_button"),
                    ) {
                        Text(if (isEditing) "ذخیره تغییرات" else "ثبت کارت بانکی", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CardTransferModalDialog(
    accountSummaries: List<AccountSummaryUi>,
    defaultFromId: Long?,
    currencyUnit: CurrencyUnit,
    usePersianDigits: Boolean,
    onDismiss: () -> Unit,
    onExecuteTransfer: (from: AccountEntity, to: AccountEntity, amountToman: Long, note: String) -> Unit,
) {
    val colors = TarazTheme.colors
    var fromId by remember(defaultFromId, accountSummaries) {
        mutableStateOf(defaultFromId ?: accountSummaries.firstOrNull()?.account?.id ?: 0L)
    }
    var toId by remember(fromId, accountSummaries) {
        mutableStateOf(
            accountSummaries.firstOrNull { it.account.id != fromId }?.account?.id
                ?: accountSummaries.lastOrNull()?.account?.id
                ?: 0L
        )
    }
    var rawAmountDigits by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val fromSummary = accountSummaries.firstOrNull { it.account.id == fromId }
    val toSummary = accountSummaries.firstOrNull { it.account.id == toId }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .widthIn(max = 480.dp)
                .glassPanel(
                    shape = RoundedCornerShape(22.dp),
                    topColor = colors.glassModalSurface,
                    bottomColor = colors.glassSurfaceTop,
                    elevation = 16.dp,
                ),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SwapHoriz,
                            contentDescription = null,
                            tint = colors.techBlue,
                        )
                        Text(
                            text = "کارت به کارت (انتقال داخلی)",
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.textPrimary,
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.cd_close_dialog))
                    }
                }

                if (accountSummaries.size < 2) {
                    Text(
                        text = "برای انتقال بین کارت‌ها حداقل به ۲ کارت بانکی نیاز دارید.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textMuted,
                    )
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.techBlue),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("متوجه شدم", color = Color.White)
                    }
                } else {
                    Text(
                        text = "از کدام کارت (مبدا)؟",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.navyAnchor,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        accountSummaries.forEach { s ->
                            ModalFilterChip(
                                label = "${s.account.name} (${
                                    PersianFormatUtils.formatCurrency(s.currentBalanceToman, currencyUnit, usePersianDigits)
                                })",
                                selected = fromId == s.account.id,
                                onClick = {
                                    fromId = s.account.id
                                    if (toId == fromId) {
                                        toId = accountSummaries.firstOrNull { it.account.id != fromId }?.account?.id ?: 0L
                                    }
                                    errorMsg = null
                                },
                            )
                        }
                    }

                    Text(
                        text = "به کدام کارت (مقصد)؟",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.navyAnchor,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        accountSummaries.filter { it.account.id != fromId }.forEach { s ->
                            ModalFilterChip(
                                label = "${s.account.name} (${
                                    PersianFormatUtils.formatCurrency(s.currentBalanceToman, currencyUnit, usePersianDigits)
                                })",
                                selected = toId == s.account.id,
                                onClick = {
                                    toId = s.account.id
                                    errorMsg = null
                                },
                            )
                        }
                    }

                    OutlinedTextField(
                        value = PersianFormatUtils.formatInputWithCommas(rawAmountDigits, usePersianDigits),
                        onValueChange = {
                            rawAmountDigits = PersianFormatUtils.toAsciiDigits(it).take(13)
                            errorMsg = null
                        },
                        label = { Text("مبلغ انتقال") },
                        trailingIcon = {
                            Text(
                                text = currencyUnit.labelFa,
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.textMuted,
                                modifier = Modifier.padding(end = 12.dp),
                            )
                        },
                        textStyle = TabularNumTextStyle.copy(fontWeight = FontWeight.Bold),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("توضیحات اختیاری") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (errorMsg != null) {
                        Text(errorMsg!!, style = MaterialTheme.typography.bodySmall, color = colors.outflow)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("انصراف", color = colors.textMuted)
                        }
                        Button(
                            onClick = {
                                val raw = rawAmountDigits.toLongOrNull() ?: 0L
                                val amountToman = if (currencyUnit == CurrencyUnit.RIAL) raw / 10L else raw
                                when {
                                    fromSummary == null || toSummary == null -> errorMsg = "کارت مبدا و مقصد را انتخاب کنید."
                                    fromSummary.account.id == toSummary.account.id -> errorMsg = "کارت مبدا و مقصد نمی‌تواند یکی باشد."
                                    amountToman <= 0L -> errorMsg = "مبلغ انتقال باید بیشتر از صفر باشد."
                                    else -> onExecuteTransfer(fromSummary.account, toSummary.account, amountToman, note)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.techBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.3f),
                        ) {
                            Text("انجام انتقال", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModalChoiceButton(
    label: String,
    selected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) activeColor.copy(alpha = 0.16f) else colors.glassInnerSheen,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) activeColor else colors.glassBorderHighlight,
        ),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier.padding(vertical = 11.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) activeColor else colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ModalFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = TarazTheme.colors
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) colors.activeNavBg else colors.glassInnerSheen,
        border = BorderStroke(1.dp, if (selected) colors.techBlue else colors.glassBorderHighlight),
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) colors.techBlue else colors.textPrimary,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
        )
    }
}
