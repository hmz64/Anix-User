package com.anix.app.core.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─── LIQUID GLASS COLOR PALETTE ──────────────────────────────
val GlassBgDark       = Color(0xFF050A18)
val GlassBgMid        = Color(0xFF0A1628)
val GlassBgAccent     = Color(0xFF0D1F3C)

val GlassSurface      = Color(0x14FFFFFF)
val GlassSurfaceHigh  = Color(0x1FFFFFFF)
val GlassBorder       = Color(0x33FFFFFF)
val GlassBorderStrong = Color(0x4DFFFFFF)

val AccentBlue        = Color(0xFF0A84FF)
val AccentBlueDim     = Color(0x330A84FF)
val AccentBlueGlow    = Color(0x550A84FF)

val TextPrimary       = Color(0xFFFFFFFF)
val TextSecondary     = Color(0xB3FFFFFF)
val TextMuted         = Color(0x66FFFFFF)

val GlassError        = Color(0xFFFF453A)
val GlassSuccess      = Color(0xFF30D158)
val GlassWarning      = Color(0xFFFFD60A)

// ─── BACKWARD-COMPATIBLE ALIASES ────────────────────────────
val BorderBlack   = GlassBorder
val Background    = GlassBgDark
val Surface       = GlassSurface
val Primary       = AccentBlue
val Secondary     = Color(0xFFFF3399)
val TextBlack     = TextPrimary
val AccentLime    = GlassSuccess
val AccentOrange  = Color(0xFFFF6633)
val Error         = GlassError

val LiquidGlassBackground = Brush.verticalGradient(
    colorStops = arrayOf(
        0.0f to GlassBgDark,
        0.4f to GlassBgMid,
        1.0f to GlassBgAccent
    )
)

val LiquidGlassTypography = Typography(
    titleLarge  = TextStyle(
        color       = TextPrimary,
        fontWeight  = FontWeight.Bold,
        fontSize    = 22.sp,
        letterSpacing = (-0.5).sp
    ),
    titleMedium = TextStyle(
        color       = TextPrimary,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 17.sp
    ),
    bodyLarge   = TextStyle(
        color       = TextPrimary,
        fontWeight  = FontWeight.Normal,
        fontSize    = 15.sp
    ),
    bodyMedium  = TextStyle(
        color       = TextSecondary,
        fontWeight  = FontWeight.Normal,
        fontSize    = 13.sp
    ),
    labelSmall  = TextStyle(
        color       = TextMuted,
        fontWeight  = FontWeight.Medium,
        fontSize    = 11.sp
    ),
)

@Composable
fun AnixTheme(content: @Composable () -> Unit) {
    val colors = darkColorScheme(
        primary          = AccentBlue,
        onPrimary        = TextPrimary,
        primaryContainer = AccentBlueDim,
        background       = GlassBgDark,
        surface          = GlassSurface,
        onBackground     = TextPrimary,
        onSurface        = TextPrimary,
        outline          = GlassBorder,
        error            = GlassError,
    )

    MaterialTheme(
        colorScheme = colors,
        typography  = LiquidGlassTypography,
        content     = content
    )
}
