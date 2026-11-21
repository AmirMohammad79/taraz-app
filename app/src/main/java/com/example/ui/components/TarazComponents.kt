package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.DonutLarge
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.SpaceDashboard
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.CATEGORY_CARD_TRANSFER
import com.example.data.local.TransactionStatus
import com.example.data.local.WorkspaceScope
import com.example.ui.theme.TabularNumTextStyle
import com.example.ui.theme.TarazTheme
import com.example.ui.viewmodel.MonthlyCashFlowPoint
import com.example.ui.viewmodel.TarazDestination
import com.example.util.CurrencyUnit
import com.example.util.PersianFormatUtils
import com.example.util.ShamsiDate

@Composable
fun TarazGlassBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = TarazTheme.colors
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                colors = listOf(
                    colors.backdropGradientStart,
                    colors.backdropGradientMid,
                    colors.backdropGradientEnd,
                ),
            )
        ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colors.orbBlue, Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.14f),
                    radius = size.maxDimension * 0.48f,
                ),
                radius = size.maxDimension * 0.48f,
                center = Offset(size.width * 0.85f, size.height * 0.14f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colors.orbEmerald, Color.Transparent),
                    center = Offset(size.width * 0.14f, size.height * 0.42f),
                    radius = size.maxDimension * 0.44f,
                ),
                radius = size.maxDimension * 0.44f,
                center = Offset(size.width * 0.14f, size.height * 0.42f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colors.orbIndigo, Color.Transparent),
                    center = Offset(size.width * 0.68f, size.height * 0.84f),
                    radius = size.maxDimension * 0.46f,
                ),
                radius = size.maxDimension * 0.46f,
                center = Offset(size.width * 0.68f, size.height * 0.84f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colors.orbWarm, Color.Transparent),
                    center = Offset(size.width * 0.25f, size.height * 0.82f),
                    radius = size.maxDimension * 0.38f,
                ),
                radius = size.maxDimension * 0.38f,
                center = Offset(size.width * 0.25f, size.height * 0.82f),
            )
        }
        content()
    }
}

@Composable
fun Modifier.glassPanel(
    shape: Shape = RoundedCornerShape(18.dp),
    topColor: Color = TarazTheme.colors.glassSurfaceTop,
    bottomColor: Color = TarazTheme.colors.glassSurfaceBottom,
    borderHighlight: Color = TarazTheme.colors.glassBorderHighlight,
    borderTrailing: Color = TarazTheme.colors.glassBorderTrailing,
    borderWidth: Dp = 1.2.dp,
    elevation: Dp = 8.dp,
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = Color(0xFF3B82F6).copy(alpha = 0.08f),
        spotColor = Color(0xFF38BDF8).copy(alpha = 0.12f),
    )
    .clip(shape)
    .background(
        Brush.linearGradient(
            colors = listOf(topColor, bottomColor),
        )
    )
    .border(
        width = borderWidth,
        brush = Brush.linearGradient(
            colors = listOf(borderHighlight, borderTrailing, borderHighlight.copy(alpha = 0.4f)),
        ),
        shape = shape,
    )

@Composable
fun TarazCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    accentGlow: Color? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    val baseModifier = modifier
        .glassPanel(shape = shape)
        .let {
            if (accentGlow != null) {
                it.drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(accentGlow.copy(alpha = 0.13f), Color.Transparent),
                            center = Offset(size.width * 0.12f, size.height * 0.12f),
                            radius = size.maxDimension * 0.65f,
                        ),
                        radius = size.maxDimension * 0.65f,
                        center = Offset(size.width * 0.12f, size.height * 0.12f),
                    )
                }
            } else {
                it
            }
        }
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }
        .padding(contentPadding)

    Column(modifier = baseModifier, content = content)
}

@Composable
fun SoftGlassIconBadge(
    icon: ImageVector,
    tint: Color,
    contentDescription: String? = null,
    size: Dp = 38.dp,
    iconSize: Dp = 20.dp,
    cornerRadius: Dp = 12.dp,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        tint.copy(alpha = 0.18f),
                        tint.copy(alpha = 0.08f),
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.85f), shape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize),
        )
    }
}

@Composable
fun CategoryGlassIconBadge(
    category: String,
    isIncome: Boolean,
    size: Dp = 38.dp,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    val (icon, tint) = when {
        category == CATEGORY_CARD_TRANSFER -> Icons.Rounded.SwapHoriz to Color(0xFF06B6D4)
        isIncome -> Icons.Rounded.Payments to colors.inflow
        category.contains("مسکن") || category.contains("اجاره") -> Icons.Rounded.Home to Color(0xFF6366F1)
        category.contains("خوراک") || category.contains("خرید") -> Icons.Rounded.ShoppingBag to colors.warning
        category.contains("نرم‌افزار") || category.contains("اشتراک") -> Icons.Rounded.Devices to colors.techBlue
        category.contains("حمل") || category.contains("خودرو") -> Icons.Rounded.DirectionsCar to Color(0xFF06B6D4)
        category.contains("بهداشت") || category.contains("درمان") -> Icons.Rounded.Favorite to colors.outflow
        category.contains("سرمایه") -> Icons.Rounded.Savings to colors.inflow
        else -> Icons.Rounded.ReceiptLong to colors.techBlue
    }
    SoftGlassIconBadge(
        icon = icon,
        tint = tint,
        contentDescription = category,
        size = size,
        iconSize = size * 0.52f,
        cornerRadius = 12.dp,
        modifier = modifier,
    )
}

/**
 * Responsive Stat Summary Card: top icon+trend row, dedicated full-width title,
 * and adaptive numeric row so no text or digit is ever hidden on compact phones.
 */
@Composable
fun StatSummaryCard(
    title: String,
    valueToman: Long,
    currencyUnit: CurrencyUnit,
    usePersianDigits: Boolean,
    icon: ImageVector = Icons.Rounded.AutoGraph,
    iconTint: Color = TarazTheme.colors.techBlue,
    trendPercent: Double? = null,
    isPositiveTrendGood: Boolean = true,
    customFormattedValue: String? = null,
    customUnitLabel: String? = null,
    accentColor: Color? = null,
    isCompact: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    TarazCard(
        modifier = modifier,
        contentPadding = PaddingValues(if (isCompact) 13.dp else 16.dp),
        accentGlow = accentColor ?: iconTint,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SoftGlassIconBadge(
                icon = icon,
                tint = iconTint,
                size = if (isCompact) 32.dp else 36.dp,
                iconSize = if (isCompact) 17.dp else 19.dp,
                cornerRadius = 10.dp,
            )
            if (trendPercent != null) {
                TrendPillBadge(
                    percent = trendPercent,
                    isPositiveGood = isPositiveTrendGood,
                    usePersianDigits = usePersianDigits,
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(6.dp))
        val formatted = customFormattedValue ?: PersianFormatUtils.formatCurrency(
            amountToman = valueToman,
            unit = currencyUnit,
            usePersianDigits = usePersianDigits,
        )
        val numSize = when {
            isCompact && formatted.length >= 11 -> 15.sp
            isCompact && formatted.length >= 9 -> 17.sp
            isCompact -> 19.sp
            else -> 22.sp
        }
        Text(
            text = formatted,
            style = MaterialTheme.typography.headlineLarge.merge(
                TabularNumTextStyle.copy(
                    fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
                    fontSize = numSize,
                    fontWeight = FontWeight.Bold,
                    lineHeight = numSize * 1.25f,
                )
            ),
            color = accentColor ?: colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = customUnitLabel ?: currencyUnit.labelFa,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
    }
}

@Composable
fun TrendPillBadge(
    percent: Double,
    isPositiveGood: Boolean,
    usePersianDigits: Boolean,
) {
    val colors = TarazTheme.colors
    val isGood = if (isPositiveGood) percent >= 0 else percent <= 0
    val bgColor = if (isGood) colors.inflowBg else colors.outflowBg
    val fgColor = if (isGood) colors.inflowText else colors.outflowText
    val borderColor = if (isGood) colors.inflow.copy(alpha = 0.35f) else colors.outflow.copy(alpha = 0.35f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .padding(horizontal = 7.dp, vertical = 3.dp),
    ) {
        Text(
            text = PersianFormatUtils.formatPercent(
                value = percent,
                includePlus = true,
                usePersianDigits = usePersianDigits,
            ),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFeatureSettings = "tnum",
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
            ),
            color = fgColor,
            maxLines = 1,
        )
    }
}

@Composable
fun StatusBadge(
    statusName: String,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    val status = TransactionStatus.entries.firstOrNull { it.name == statusName }
    val (label, bg, fg, rim) = when (status) {
        TransactionStatus.VERIFIED -> listOf("تایید شده", colors.inflowBg, colors.inflowText, colors.inflow.copy(alpha = 0.35f))
        TransactionStatus.PENDING -> listOf("در انتظار", colors.warningBg, colors.warningText, colors.warning.copy(alpha = 0.35f))
        TransactionStatus.OVERDUE -> listOf("بیش از بودجه", colors.outflowBg, colors.outflowText, colors.outflow.copy(alpha = 0.35f))
        null -> listOf(statusName, colors.neutralBadgeBg, colors.neutralBadgeText, colors.border)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg as Color)
            .border(1.dp, rim as Color, RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 3.dp),
    ) {
        Text(
            text = label as String,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = fg as Color,
            maxLines = 1,
        )
    }
}

@Composable
fun NeutralTagBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(colors.neutralBadgeBg)
            .border(1.dp, colors.glassBorderHighlight.copy(alpha = 0.7f), RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = colors.neutralBadgeText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun RtlBudgetProgressBar(
    ratio: Float,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    val clamped = ratio.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = clamped,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 350f),
        label = "budget_progress",
    )
    val (startColor, endColor) = when {
        ratio <= 0.75f -> colors.inflow to colors.inflowGlow
        ratio <= 0.90f -> colors.warning to colors.warningGlow
        else -> colors.outflow to colors.outflowGlow
    }
    val animStart by animateColorAsState(targetValue = startColor, label = "budget_color_start")
    val animEnd by animateColorAsState(targetValue = endColor, label = "budget_color_end")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(50))
            .background(colors.progressTrack)
            .border(1.dp, colors.glassBorderHighlight.copy(alpha = 0.7f), RoundedCornerShape(50)),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (animatedProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedProgress)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(animEnd, animStart),
                        )
                    ),
            )
        }
    }
}

@Composable
fun CashFlowBarChart(
    points: List<MonthlyCashFlowPoint>,
    currencyUnit: CurrencyUnit,
    usePersianDigits: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    var selectedIdx by remember(points) { mutableIntStateOf((points.size - 1).coerceAtLeast(0)) }

    Column(modifier = modifier) {
        if (points.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "داده‌ای برای نمایش جریان نقدی موجود نیست",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted,
                )
            }
            return
        }

        val activePoint = points.getOrNull(selectedIdx)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LegendDotLabel(color = colors.inflow, label = "ورودی")
                    LegendDotLabel(color = colors.techBlue, label = "خروجی")
                }
                if (activePoint != null) {
                    NeutralTagBadge(text = "ماه انتخابی: ${activePoint.monthLabel}")
                }
            }

            if (activePoint != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.glassInnerSheen)
                        .border(1.dp, colors.glassBorderHighlight.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "درآمد: +${PersianFormatUtils.formatCurrency(activePoint.inflowToman, currencyUnit, usePersianDigits)}",
                        style = TabularNumTextStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                        color = colors.inflowText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "هزینه: -${PersianFormatUtils.formatCurrency(activePoint.outflowToman, currencyUnit, usePersianDigits)}",
                        style = TabularNumTextStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                        color = colors.outflowText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        val maxAmount = remember(points) {
            points.maxOfOrNull { maxOf(it.inflowToman, it.outflowToman) }?.coerceAtLeast(1L) ?: 1L
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .pointerInput(points) {
                    detectTapGestures { offset ->
                        if (points.isNotEmpty()) {
                            val groupWidth = size.width / points.size.toFloat()
                            val tappedFromLeft = (offset.x / groupWidth).toInt().coerceIn(0, points.lastIndex)
                            selectedIdx = points.lastIndex - tappedFromLeft
                        }
                    }
                },
        ) {
            val chartHeight = size.height - 8.dp.toPx()
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            for (step in 0..3) {
                val y = chartHeight * (step / 3f)
                drawLine(
                    color = colors.border,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = if (step < 3) dashEffect else null,
                )
            }

            val groupCount = points.size
            val groupWidth = size.width / groupCount.toFloat()
            val barWidth = (groupWidth * 0.24f).coerceIn(8.dp.toPx(), 18.dp.toPx())
            val barGap = 4.dp.toPx()

            points.forEachIndexed { idx, point ->
                val rtlSlot = (groupCount - 1) - idx
                val centerX = rtlSlot * groupWidth + groupWidth / 2f

                if (idx == selectedIdx) {
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(colors.activeNavBg, colors.glassInnerSheen),
                        ),
                        topLeft = Offset(rtlSlot * groupWidth + 3.dp.toPx(), 0f),
                        size = Size(groupWidth - 6.dp.toPx(), chartHeight),
                        cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                    )
                }

                val inflowHeight = ((point.inflowToman.toFloat() / maxAmount.toFloat()) * (chartHeight - 10.dp.toPx()))
                    .coerceAtLeast(if (point.inflowToman > 0) 4.dp.toPx() else 0f)
                val outflowHeight = ((point.outflowToman.toFloat() / maxAmount.toFloat()) * (chartHeight - 10.dp.toPx()))
                    .coerceAtLeast(if (point.outflowToman > 0) 4.dp.toPx() else 0f)

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(colors.inflowGlow, colors.inflow),
                    ),
                    topLeft = Offset(centerX + barGap / 2f, chartHeight - inflowHeight),
                    size = Size(barWidth, inflowHeight),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                )

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(colors.techBlueGlow, colors.techBlue),
                    ),
                    topLeft = Offset(centerX - barWidth - barGap / 2f, chartHeight - outflowHeight),
                    size = Size(barWidth, outflowHeight),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            points.forEachIndexed { idx, point ->
                val isSelected = idx == selectedIdx
                Text(
                    text = point.monthLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 10.sp,
                    ),
                    color = if (isSelected) colors.techBlue else colors.textMuted,
                    maxLines = 1,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedIdx = idx }
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun LegendDotLabel(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TarazTheme.colors.textMuted,
        )
    }
}

@Composable
fun TarazTopHeaderBar(
    activeDestination: TarazDestination,
    shamsiDate: ShamsiDate,
    workspaceFilter: WorkspaceScope,
    currencyUnit: CurrencyUnit,
    usePersianDigits: Boolean,
    showMenuButton: Boolean,
    isCompact: Boolean,
    onOpenMenu: () -> Unit,
    onSelectWorkspace: (WorkspaceScope) -> Unit,
    onToggleCurrency: () -> Unit,
    onNewTransaction: () -> Unit,
) {
    val colors = TarazTheme.colors
    var showWorkspaceMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colors.glassSurfaceTop, colors.glassSurfaceBottom),
                )
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .padding(horizontal = if (isCompact) 12.dp else 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                if (showMenuButton) {
                    IconButton(
                        onClick = onOpenMenu,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("open_drawer_button"),
                    ) {
                        SoftGlassIconBadge(
                            icon = Icons.Rounded.Menu,
                            tint = colors.techBlue,
                            contentDescription = stringResource(R.string.cd_open_drawer),
                            size = 36.dp,
                            iconSize = 20.dp,
                            cornerRadius = 11.dp,
                        )
                    }
                }

                if (isCompact) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = activeDestination.fullTitleFa,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = colors.navyAnchor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${shamsiDate.dayOfWeekName}، ${shamsiDate.toHeaderDisplay(usePersianDigits)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SoftGlassIconBadge(
                            icon = Icons.Rounded.CalendarMonth,
                            tint = colors.techBlue,
                            size = 34.dp,
                            iconSize = 18.dp,
                            cornerRadius = 10.dp,
                        )
                        Column {
                            Text(
                                text = shamsiDate.toHeaderDisplay(usePersianDigits),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = shamsiDate.dayOfWeekName,
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textMuted,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = colors.glassInnerSheen,
                            border = BorderStroke(1.dp, colors.glassBorderHighlight),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { showWorkspaceMenu = true }
                                .testTag("workspace_switcher"),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(
                                    text = workspaceFilter.labelFa,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colors.textPrimary,
                                )
                                Icon(
                                    imageVector = Icons.Rounded.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = colors.textMuted,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = showWorkspaceMenu,
                            onDismissRequest = { showWorkspaceMenu = false },
                        ) {
                            WorkspaceScope.entries.forEach { scope ->
                                DropdownMenuItem(
                                    text = { Text(scope.labelFa, style = MaterialTheme.typography.bodyMedium) },
                                    onClick = {
                                        onSelectWorkspace(scope)
                                        showWorkspaceMenu = false
                                    },
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = colors.glassInnerSheen,
                    border = BorderStroke(1.dp, colors.glassBorderHighlight),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onToggleCurrency)
                        .testTag("currency_toggle_button"),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CurrencyExchange,
                            contentDescription = null,
                            tint = colors.techBlue,
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = currencyUnit.labelFa,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.navyAnchor,
                            maxLines = 1,
                        )
                    }
                }

                if (!isCompact) {
                    Button(
                        onClick = onNewTransaction,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.techBlue,
                            contentColor = Color.White,
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.55f)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 9.dp),
                        modifier = Modifier.testTag("new_transaction_cta"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddCircle,
                            contentDescription = stringResource(R.string.cd_add_transaction),
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.action_new_transaction),
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
        HorizontalDivider(thickness = 1.dp, color = colors.glassBorderHighlight.copy(alpha = 0.8f))
    }
}

/**
 * Professional Floating Frosted-Glass Fintech Navigation Dock with center glowing quick-add orb.
 */
@Composable
fun TarazFloatingBottomNavBar(
    activeDestination: TarazDestination,
    pendingCount: Int,
    usePersianDigits: Boolean,
    onSelectDestination: (TarazDestination) -> Unit,
    onQuickAddTransaction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    val dockShape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        // Glass floating pill bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = dockShape,
                    ambientColor = colors.techBlue.copy(alpha = 0.18f),
                    spotColor = colors.techBlue.copy(alpha = 0.24f),
                )
                .clip(dockShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colors.glassModalSurface,
                            colors.glassSurfaceTop,
                        )
                    )
                )
                .border(
                    width = 1.3.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.98f),
                            colors.techBlueGlow.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.90f),
                        )
                    ),
                    shape = dockShape,
                )
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            FloatingDockItem(
                destination = TarazDestination.DASHBOARD,
                selected = activeDestination == TarazDestination.DASHBOARD,
                badgeCount = null,
                usePersianDigits = usePersianDigits,
                onClick = { onSelectDestination(TarazDestination.DASHBOARD) },
                modifier = Modifier.weight(1f),
            )
            FloatingDockItem(
                destination = TarazDestination.LEDGER,
                selected = activeDestination == TarazDestination.LEDGER,
                badgeCount = pendingCount.takeIf { it > 0 },
                usePersianDigits = usePersianDigits,
                onClick = { onSelectDestination(TarazDestination.LEDGER) },
                modifier = Modifier.weight(1f),
            )

            // Center spacer reserved for the elevated glowing '+' CTA button
            Spacer(modifier = Modifier.width(64.dp))

            FloatingDockItem(
                destination = TarazDestination.BUDGETS,
                selected = activeDestination == TarazDestination.BUDGETS,
                badgeCount = null,
                usePersianDigits = usePersianDigits,
                onClick = { onSelectDestination(TarazDestination.BUDGETS) },
                modifier = Modifier.weight(1f),
            )
            FloatingDockItem(
                destination = TarazDestination.ACCOUNTS,
                selected = activeDestination == TarazDestination.ACCOUNTS,
                badgeCount = null,
                usePersianDigits = usePersianDigits,
                onClick = { onSelectDestination(TarazDestination.ACCOUNTS) },
                modifier = Modifier.weight(1f),
            )
        }

        // Elevated Center Quick-Add Gradient Orb
        Box(
            modifier = Modifier
                .offset(y = (-14).dp)
                .size(58.dp)
                .shadow(
                    elevation = 14.dp,
                    shape = CircleShape,
                    ambientColor = colors.techBlue.copy(alpha = 0.38f),
                    spotColor = colors.inflow.copy(alpha = 0.36f),
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(colors.techBlue, colors.techBlueGlow, colors.inflow),
                    )
                )
                .border(2.5.dp, Color.White.copy(alpha = 0.95f), CircleShape)
                .clickable(onClick = onQuickAddTransaction)
                .testTag("new_transaction_cta"),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = stringResource(R.string.cd_add_transaction),
                tint = Color.White,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

@Composable
private fun FloatingDockItem(
    destination: TarazDestination,
    selected: Boolean,
    badgeCount: Int?,
    usePersianDigits: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    val pillBg by animateColorAsState(
        targetValue = if (selected) colors.activeNavBg else Color.Transparent,
        label = "dock_bg",
    )
    val contentTint by animateColorAsState(
        targetValue = if (selected) colors.techBlue else colors.textMuted,
        label = "dock_tint",
    )

    Column(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(pillBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .testTag("bottom_nav_${destination.name}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = destination.icon(),
                contentDescription = destination.labelFa,
                tint = contentTint,
                modifier = Modifier.size(22.dp),
            )
            if (badgeCount != null) {
                Box(
                    modifier = Modifier
                        .offset(x = 6.dp, y = (-3).dp)
                        .clip(CircleShape)
                        .background(colors.outflow)
                        .padding(horizontal = 4.dp, vertical = 0.5.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = PersianFormatUtils.formatNumber(badgeCount.toLong(), usePersianDigits),
                        style = TabularNumTextStyle.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = Color.White,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = destination.labelFa,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            ),
            color = contentTint,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun TarazSidebarContent(
    activeDestination: TarazDestination,
    pendingCount: Int,
    collapsed: Boolean,
    usePersianDigits: Boolean,
    workspaceFilter: WorkspaceScope,
    onSelectWorkspace: (WorkspaceScope) -> Unit,
    onSelectDestination: (TarazDestination) -> Unit,
    onToggleCollapse: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val colors = TarazTheme.colors
    Row(
        modifier = modifier
            .fillMaxHeight()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colors.glassSurfaceTop, colors.glassSurfaceBottom),
                )
            ),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (collapsed) 12.dp else 18.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (collapsed) Arrangement.Center else Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(colors.techBlue, colors.techBlueGlow, colors.inflowGlow),
                                )
                            )
                            .border(1.2.dp, Color.White.copy(alpha = 0.75f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    if (!collapsed) {
                        Column {
                            Text(
                                text = stringResource(R.string.brand_title_fa),
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = colors.navyAnchor,
                            )
                            Text(
                                text = stringResource(R.string.brand_subtitle_fa),
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textMuted,
                            )
                        }
                    }
                }

                if (!collapsed && onToggleCollapse != null) {
                    IconButton(onClick = onToggleCollapse) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = stringResource(R.string.cd_collapse_drawer),
                            tint = colors.textMuted,
                        )
                    }
                }
            }

            if (collapsed && onToggleCollapse != null) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    IconButton(onClick = onToggleCollapse) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronLeft,
                            contentDescription = stringResource(R.string.cd_open_drawer),
                            tint = colors.textMuted,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = colors.glassBorderHighlight.copy(alpha = 0.7f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            TarazDestination.entries.forEach { dest ->
                val selected = dest == activeDestination
                val badgeCount = if (dest == TarazDestination.LEDGER && pendingCount > 0) pendingCount else null
                SidebarNavItem(
                    label = dest.fullTitleFa,
                    icon = dest.icon(),
                    selected = selected,
                    collapsed = collapsed,
                    badgeCount = badgeCount,
                    usePersianDigits = usePersianDigits,
                    onClick = { onSelectDestination(dest) },
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!collapsed) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "فضای کاری فعال",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                    WorkspaceScope.entries.forEach { scope ->
                        val active = scope == workspaceFilter
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (active) colors.activeNavBg else colors.glassInnerSheen)
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (active) colors.techBlue.copy(alpha = 0.5f) else colors.glassBorderHighlight,
                                    ),
                                    RoundedCornerShape(14.dp),
                                )
                                .clickable { onSelectWorkspace(scope) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = scope.labelFa,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (active) colors.techBlue else colors.textPrimary,
                            )
                            if (active) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(colors.techBlue),
                                )
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(colors.glassBorderHighlight.copy(alpha = 0.85f)),
        )
    }
}

@Composable
private fun SidebarNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    collapsed: Boolean,
    badgeCount: Int?,
    usePersianDigits: Boolean,
    onClick: () -> Unit,
) {
    val colors = TarazTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (collapsed) 8.dp else 12.dp, vertical = 3.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) colors.activeNavBg else Color.Transparent)
            .let {
                if (selected) {
                    it.border(1.dp, colors.techBlue.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                } else {
                    it
                }
            }
            .clickable(onClick = onClick)
            .testTag("nav_item_${label}"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(26.dp)
                .clip(RoundedCornerShape(50))
                .background(if (selected) colors.techBlue else Color.Transparent),
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = if (collapsed) 4.dp else 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (collapsed) Arrangement.Center else Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = if (collapsed) Modifier else Modifier.weight(1f),
            ) {
                SoftGlassIconBadge(
                    icon = icon,
                    tint = if (selected) colors.techBlue else colors.textMuted,
                    contentDescription = label,
                    size = 32.dp,
                    iconSize = 18.dp,
                    cornerRadius = 10.dp,
                )
                if (!collapsed) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        ),
                        color = if (selected) colors.techBlue else colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            if (!collapsed && badgeCount != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(colors.outflowBg)
                        .border(1.dp, colors.outflow.copy(alpha = 0.35f), RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = PersianFormatUtils.formatNumber(badgeCount.toLong(), usePersianDigits),
                        style = TabularNumTextStyle.copy(
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = colors.outflowText,
                    )
                }
            }
        }
    }
}

fun TarazDestination.icon(): ImageVector = when (this) {
    TarazDestination.DASHBOARD -> Icons.Rounded.SpaceDashboard
    TarazDestination.LEDGER -> Icons.Rounded.ReceiptLong
    TarazDestination.BUDGETS -> Icons.Rounded.DonutLarge
    TarazDestination.ACCOUNTS -> Icons.Rounded.CreditCard
}
