package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private val TarazLightColorScheme = lightColorScheme(
    primary = SkyBright,
    onPrimary = Color.White,
    primaryContainer = PrimaryFixed,
    onPrimaryContainer = PrimaryDeep,
    secondary = IndigoAccent,
    onSecondary = Color.White,
    secondaryContainer = ActiveNavFill,
    onSecondaryContainer = PrimaryDeep,
    tertiary = EmeraldInflow,
    onTertiary = Color.White,
    tertiaryContainer = EmeraldBadgeBg,
    onTertiaryContainer = EmeraldBadgeText,
    error = CoralSoft,
    onError = Color.White,
    errorContainer = CrimsonBadgeBg,
    onErrorContainer = CrimsonBadgeText,
    background = CanvasLight,
    onBackground = TextSlateDark,
    surface = Color(0xE0FFFFFF),
    onSurface = TextSlateDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSlateMuted,
    outline = OutlineMuted,
    outlineVariant = BorderLight,
)

private val TarazDarkColorScheme = darkColorScheme(
    primary = DarkTechBlue,
    onPrimary = DarkCanvas,
    primaryContainer = DarkSurfaceElevated,
    onPrimaryContainer = DarkTextPrimary,
    secondary = DarkTechBlue,
    onSecondary = DarkCanvas,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = DarkTextPrimary,
    tertiary = Color(0xFF34D399),
    onTertiary = DarkCanvas,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFA7F3D0),
    error = Color(0xFFFB7185),
    onError = DarkCanvas,
    errorContainer = Color(0xFF4C0519),
    onErrorContainer = Color(0xFFFECDD3),
    background = DarkCanvas,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextMuted,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
)

val TarazShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

object TarazTheme {
    val colors: TarazSemanticColors
        @Composable
        @ReadOnlyComposable
        get() = LocalTarazColors.current
}

@Composable
fun TarazPrecisionTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) TarazDarkColorScheme else TarazLightColorScheme
    val semanticColors = if (darkTheme) DarkTarazSemanticColors else LightTarazSemanticColors

    CompositionLocalProvider(
        LocalTarazColors provides semanticColors,
        LocalLayoutDirection provides LayoutDirection.Rtl,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = TarazTypography,
            shapes = TarazShapes,
            content = content,
        )
    }
}
