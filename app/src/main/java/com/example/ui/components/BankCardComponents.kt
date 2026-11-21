package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Contactless
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AccountEntity
import com.example.data.local.AccountKind
import com.example.ui.theme.TabularNumTextStyle
import com.example.ui.theme.TarazTheme
import com.example.ui.viewmodel.AccountSummaryUi
import com.example.util.CurrencyUnit
import com.example.util.PersianFormatUtils

data class BankCardPalette(
    val nameFa: String,
    val start: Color,
    val mid: Color,
    val end: Color,
    val accent: Color,
)

val TarazCardPalettes = listOf(
    BankCardPalette(
        nameFa = "آبی اقیانوسی",
        start = Color(0xFF0284C7),
        mid = Color(0xFF0EA5E9),
        end = Color(0xFF38BDF8),
        accent = Color(0xFF0284C7),
    ),
    BankCardPalette(
        nameFa = "سبز زمردی",
        start = Color(0xFF059669),
        mid = Color(0xFF10B981),
        end = Color(0xFF34D399),
        accent = Color(0xFF059669),
    ),
    BankCardPalette(
        nameFa = "نیلی مدرن",
        start = Color(0xFF4338CA),
        mid = Color(0xFF6366F1),
        end = Color(0xFF818CF8),
        accent = Color(0xFF4F46E5),
    ),
    BankCardPalette(
        nameFa = "طلایی گرم",
        start = Color(0xFFD97706),
        mid = Color(0xFFF59E0B),
        end = Color(0xFFFBBF24),
        accent = Color(0xFFD97706),
    ),
    BankCardPalette(
        nameFa = "مرجانی روشن",
        start = Color(0xFFE11D48),
        mid = Color(0xFFF43F5E),
        end = Color(0xFFFB7185),
        accent = Color(0xFFE11D48),
    ),
    BankCardPalette(
        nameFa = "بنفش رویایی",
        start = Color(0xFF7C3AED),
        mid = Color(0xFF8B5CF6),
        end = Color(0xFFA78BFA),
        accent = Color(0xFF7C3AED),
    ),
)

fun cardPaletteFor(colorIndex: Int): BankCardPalette {
    val safeIndex = ((colorIndex % TarazCardPalettes.size) + TarazCardPalettes.size) % TarazCardPalettes.size
    return TarazCardPalettes[safeIndex]
}

fun formatCardNumberForDisplay(account: AccountEntity, usePersianDigits: Boolean): String {
    val source = account.cardNumber.ifBlank { account.ibanMasked }
    val digitsOnly = PersianFormatUtils.toAsciiDigits(source)
    val formatted = if (digitsOnly.length == 16) {
        digitsOnly.chunked(4).joinToString("  ")
    } else if (source.isNotBlank()) {
        source
    } else {
        "••••  ••••  ••••  ••••"
    }
    return if (usePersianDigits) PersianFormatUtils.toPersianDigits(formatted) else formatted
}

@Composable
fun CardSourcePill(
    account: AccountEntity?,
    isIncome: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    val palette = cardPaletteFor(account?.cardColorIndex ?: 0)
    val label = if (account != null) {
        val verb = if (isIncome) "واریز به" else "برداشت از"
        "$verb ${account.name}"
    } else {
        "کارت نامشخص"
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(palette.accent.copy(alpha = 0.11f))
            .border(1.dp, palette.accent.copy(alpha = 0.28f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(palette.accent),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun MiniBankCardItem(
    summary: AccountSummaryUi,
    currencyUnit: CurrencyUnit,
    usePersianDigits: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = cardPaletteFor(summary.account.cardColorIndex)
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .width(236.dp)
            .height(142.dp)
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = palette.start.copy(alpha = 0.25f),
                spotColor = palette.mid.copy(alpha = 0.30f),
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(palette.start, palette.mid, palette.end),
                )
            )
            .border(1.2.dp, Color.White.copy(alpha = 0.45f), shape)
            .clickable(onClick = onClick)
            .testTag("dashboard_card_${summary.account.id}"),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.14f),
                radius = size.height * 0.65f,
                center = Offset(size.width * 0.12f, size.height * 0.15f),
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.10f),
                radius = size.height * 0.55f,
                center = Offset(size.width * 0.92f, size.height * 0.92f),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = summary.account.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = summary.account.bankName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.Contactless,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "موجودی فعلی",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color.White.copy(alpha = 0.82f),
                )
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        text = PersianFormatUtils.formatCurrency(
                            summary.currentBalanceToman,
                            currencyUnit,
                            usePersianDigits,
                        ),
                        style = TabularNumTextStyle.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = currencyUnit.labelFa,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(bottom = 2.dp),
                    )
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = formatCardNumberForDisplay(summary.account, usePersianDigits),
                    style = TabularNumTextStyle.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = Color.White.copy(alpha = 0.88f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun AddMiniBankCardTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .width(136.dp)
            .height(142.dp)
            .glassPanel(shape = shape, elevation = 4.dp)
            .clickable(onClick = onClick)
            .testTag("dashboard_add_card_tile"),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(12.dp),
        ) {
            SoftGlassIconBadge(
                icon = Icons.Rounded.Add,
                tint = colors.techBlue,
                size = 42.dp,
                iconSize = 22.dp,
                cornerRadius = 14.dp,
            )
            Text(
                text = "افزودن کارت",
                style = MaterialTheme.typography.labelLarge,
                color = colors.techBlue,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun FullBankCardWidget(
    summary: AccountSummaryUi,
    currencyUnit: CurrencyUnit,
    usePersianDigits: Boolean,
    onExpenseFromCard: () -> Unit,
    onIncomeToCard: () -> Unit,
    onTransferFromCard: () -> Unit,
    onViewCardLedger: () -> Unit,
    onEditCard: () -> Unit,
    onDeleteCard: () -> Unit,
    onReconcile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    val palette = cardPaletteFor(summary.account.cardColorIndex)
    val kind = AccountKind.entries.firstOrNull { it.name == summary.account.accountKind } ?: AccountKind.CHECKING
    val cardIcon = when (kind) {
        AccountKind.CHECKING -> Icons.Rounded.CreditCard
        AccountKind.SAVINGS -> Icons.Rounded.Savings
        AccountKind.CASH -> Icons.Rounded.AccountBalanceWallet
        AccountKind.BUSINESS -> Icons.Rounded.CreditCard
    }

    TarazCard(
        contentPadding = PaddingValues(14.dp),
        modifier = modifier.testTag("bank_card_widget_${summary.account.id}"),
    ) {
        // Visual Bank Card Face
        val faceShape = RoundedCornerShape(18.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = faceShape,
                    ambientColor = palette.start.copy(alpha = 0.22f),
                    spotColor = palette.mid.copy(alpha = 0.28f),
                )
                .clip(faceShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(palette.start, palette.mid, palette.end),
                    )
                )
                .border(1.2.dp, Color.White.copy(alpha = 0.45f), faceShape),
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.14f),
                    radius = size.maxDimension * 0.36f,
                    center = Offset(size.width * 0.10f, size.height * 0.12f),
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.10f),
                    radius = size.maxDimension * 0.30f,
                    center = Offset(size.width * 0.90f, size.height * 0.88f),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.22f))
                                .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = cardIcon,
                                contentDescription = summary.account.name,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = summary.account.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = "${summary.account.bankName} • ${kind.labelFa}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.86f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onEditCard,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("edit_card_${summary.account.id}"),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "ویرایش کارت و موجودی",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        IconButton(
                            onClick = onDeleteCard,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("delete_card_${summary.account.id}"),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteOutline,
                                contentDescription = "حذف کارت",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                // Live Balance
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "موجودی قابل برداشت",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.84f),
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = PersianFormatUtils.formatCurrency(
                                summary.currentBalanceToman,
                                currencyUnit,
                                usePersianDigits,
                            ),
                            style = TabularNumTextStyle.copy(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = currencyUnit.labelFa,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.92f),
                            modifier = Modifier.padding(bottom = 2.dp),
                        )
                    }
                }

                // Card number & workspace chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = formatCardNumberForDisplay(summary.account, usePersianDigits),
                            style = TabularNumTextStyle.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                            color = Color.White.copy(alpha = 0.94f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.22f))
                            .padding(horizontal = 9.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = if (summary.account.workspace == "BUSINESS") "کسب‌وکار" else "شخصی",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Monthly card inflow / outflow strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.glassInnerSheen)
                .border(1.dp, colors.glassBorderHighlight.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ورودی این ماه",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
                Text(
                    text = "+${PersianFormatUtils.formatCurrency(summary.monthlyInflowToman, currencyUnit, usePersianDigits)}",
                    style = TabularNumTextStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                    color = colors.inflowText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(26.dp)
                    .background(colors.border),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Text(
                    text = "برداشت این ماه",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
                Text(
                    text = "-${PersianFormatUtils.formatCurrency(summary.monthlyOutflowToman, currencyUnit, usePersianDigits)}",
                    style = TabularNumTextStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                    color = colors.outflowText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick operational actions for this card (2 clean rows so text NEVER clips!)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CardActionPillButton(
                label = "ثبت برداشت",
                icon = Icons.Rounded.ArrowUpward,
                tint = colors.outflow,
                onClick = onExpenseFromCard,
                modifier = Modifier.weight(1f),
            )
            CardActionPillButton(
                label = "ثبت واریز",
                icon = Icons.Rounded.ArrowDownward,
                tint = colors.inflow,
                onClick = onIncomeToCard,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CardActionPillButton(
                label = "کارت به کارت",
                icon = Icons.Rounded.SwapHoriz,
                tint = colors.techBlue,
                onClick = onTransferFromCard,
                modifier = Modifier.weight(1f),
            )
            CardActionPillButton(
                label = "گردش کارت (${PersianFormatUtils.formatNumber(summary.transactionCount.toLong(), usePersianDigits)})",
                icon = Icons.Rounded.ReceiptLong,
                tint = Color(0xFF6366F1),
                onClick = onViewCardLedger,
                modifier = Modifier.weight(1f),
            )
        }

        if (summary.pendingCount > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.warningBg)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${PersianFormatUtils.formatNumber(summary.pendingCount.toLong(), usePersianDigits)} تراکنش در انتظار تایید",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.warningText,
                    modifier = Modifier.weight(1f),
                )
                Button(
                    onClick = onReconcile,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.techBlue,
                        contentColor = Color.White,
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تایید همه", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun CardActionPillButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = tint.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.26f)),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
