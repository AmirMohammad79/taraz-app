package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Bright & Friendly Glassmorphism Tokens
val SkyPrimary = Color(0xFF2563EB)
val SkyBright = Color(0xFF3B82F6)
val SkyGlow = Color(0xFF38BDF8)
val IndigoAccent = Color(0xFF6366F1)
val PrimaryDeep = Color(0xFF1D4ED8)
val PrimaryFixed = Color(0xFFE0F2FE)
val ActiveNavFill = Color(0xFFEFF6FF)

val CanvasLight = Color(0xFFF7FAFF)
val SurfaceBright = Color(0xFFFFFFFF)
val BorderLight = Color(0xFFE2E8F0)
val OutlineMuted = Color(0xFF94A3B8)

val TextSlateDark = Color(0xFF1E293B)
val TextSlateMuted = Color(0xFF64748B)

// Friendly Semantic Financial Colors
val EmeraldInflow = Color(0xFF10B981)
val EmeraldGlow = Color(0xFF34D399)
val EmeraldBadgeBg = Color(0xFFE6FDF5)
val EmeraldBadgeText = Color(0xFF059669)

val WarmAmber = Color(0xFFF59E0B)
val AmberGlow = Color(0xFFFCD34D)
val AmberBadgeBg = Color(0xFFFFFBEB)
val AmberBadgeText = Color(0xFFD97706)

val CoralSoft = Color(0xFFF43F5E)
val CoralGlow = Color(0xFFFB7185)
val CrimsonBadgeBg = Color(0xFFFFF1F2)
val CrimsonBadgeText = Color(0xFFE11D48)

val GeneralBadgeBg = Color(0xCCEEF2FF)
val GeneralBadgeText = Color(0xFF475569)

// Brighter Twilight Theme Tokens (if dark mode is ever requested explicitly)
val DarkCanvas = Color(0xFF0F172A)
val DarkSurface = Color(0xFF1E293B)
val DarkSurfaceElevated = Color(0xFF293548)
val DarkBorder = Color(0xFF334155)
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextMuted = Color(0xFFCBD5E1)
val DarkTechBlue = Color(0xFF60A5FA)

@Immutable
data class TarazSemanticColors(
    val canvasBackground: Color,
    val backdropGradientStart: Color,
    val backdropGradientMid: Color,
    val backdropGradientEnd: Color,
    val orbBlue: Color,
    val orbEmerald: Color,
    val orbIndigo: Color,
    val orbWarm: Color,
    val glassSurfaceTop: Color,
    val glassSurfaceBottom: Color,
    val glassModalSurface: Color,
    val glassBorderHighlight: Color,
    val glassBorderTrailing: Color,
    val glassInnerSheen: Color,
    val cardSurface: Color,
    val tableHeaderSurface: Color,
    val border: Color,
    val navyAnchor: Color,
    val techBlue: Color,
    val techBlueGlow: Color,
    val activeNavBg: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val inflow: Color,
    val inflowGlow: Color,
    val inflowBg: Color,
    val inflowText: Color,
    val warning: Color,
    val warningGlow: Color,
    val warningBg: Color,
    val warningText: Color,
    val outflow: Color,
    val outflowGlow: Color,
    val outflowBg: Color,
    val outflowText: Color,
    val neutralBadgeBg: Color,
    val neutralBadgeText: Color,
    val progressTrack: Color,
)

val LightTarazSemanticColors = TarazSemanticColors(
    canvasBackground = CanvasLight,
    backdropGradientStart = Color(0xFFF8FBFF),
    backdropGradientMid = Color(0xFFF0F7FF),
    backdropGradientEnd = Color(0xFFF2FCF8),
    orbBlue = Color(0x3D38BDF8),           // Soft airy sky cyan
    orbEmerald = Color(0x3334D399),        // Fresh mint glow
    orbIndigo = Color(0x2BA78BFA),         // Gentle lavender tint
    orbWarm = Color(0x2EFDBA74),           // Subtle warm apricot sunlight
    glassSurfaceTop = Color(0xE0FFFFFF),   // ~88% crisp bright frost
    glassSurfaceBottom = Color(0xB3FFFFFF),// ~70% translucent pearl
    glassModalSurface = Color(0xF2FFFFFF), // ~95% luminous modal crystal
    glassBorderHighlight = Color(0xFFFFFFFF),
    glassBorderTrailing = Color(0x66BAE6FD),
    glassInnerSheen = Color(0x99FFFFFF),
    cardSurface = Color(0xD6FFFFFF),
    tableHeaderSurface = Color(0xB3F0F7FF),
    border = Color(0x80CBD5E1),
    navyAnchor = PrimaryDeep,
    techBlue = SkyBright,
    techBlueGlow = SkyGlow,
    activeNavBg = Color(0x1F3B82F6),
    textPrimary = TextSlateDark,
    textMuted = TextSlateMuted,
    inflow = EmeraldInflow,
    inflowGlow = EmeraldGlow,
    inflowBg = EmeraldBadgeBg,
    inflowText = EmeraldBadgeText,
    warning = WarmAmber,
    warningGlow = AmberGlow,
    warningBg = AmberBadgeBg,
    warningText = AmberBadgeText,
    outflow = CoralSoft,
    outflowGlow = CoralGlow,
    outflowBg = CrimsonBadgeBg,
    outflowText = CrimsonBadgeText,
    neutralBadgeBg = GeneralBadgeBg,
    neutralBadgeText = GeneralBadgeText,
    progressTrack = Color(0xB3E2E8F0),
)

val DarkTarazSemanticColors = TarazSemanticColors(
    canvasBackground = DarkCanvas,
    backdropGradientStart = Color(0xFF111C38),
    backdropGradientMid = Color(0xFF172548),
    backdropGradientEnd = Color(0xFF102A3A),
    orbBlue = Color(0x5238BDF8),
    orbEmerald = Color(0x4034D399),
    orbIndigo = Color(0x47818CF8),
    orbWarm = Color(0x2EFB923C),
    glassSurfaceTop = Color(0x991E293B),
    glassSurfaceBottom = Color(0x6B0F172A),
    glassModalSurface = Color(0xE81E293B),
    glassBorderHighlight = Color(0x52FFFFFF),
    glassBorderTrailing = Color(0x407DD3FC),
    glassInnerSheen = Color(0x2BFFFFFF),
    cardSurface = Color(0x851E293B),
    tableHeaderSurface = Color(0x70293548),
    border = Color(0x5294A3B8),
    navyAnchor = Color(0xFF93C5FD),
    techBlue = DarkTechBlue,
    techBlueGlow = Color(0xFF7DD3FC),
    activeNavBg = Color(0x383B82F6),
    textPrimary = DarkTextPrimary,
    textMuted = DarkTextMuted,
    inflow = Color(0xFF34D399),
    inflowGlow = Color(0xFF6EE7B7),
    inflowBg = Color(0x99064E3B),
    inflowText = Color(0xFFA7F3D0),
    warning = Color(0xFFFBBF24),
    warningGlow = Color(0xFFFDE68A),
    warningBg = Color(0x99451A03),
    warningText = Color(0xFFFDE68A),
    outflow = Color(0xFFFB7185),
    outflowGlow = Color(0xFFFDA4AF),
    outflowBg = Color(0x994C0519),
    outflowText = Color(0xFFFECDD3),
    neutralBadgeBg = Color(0x80334155),
    neutralBadgeText = DarkTextMuted,
    progressTrack = Color(0x660F172A),
)

val LocalTarazColors = staticCompositionLocalOf { LightTarazSemanticColors }
