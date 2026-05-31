package com.mysterywalk.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────
//  Neon Explorer ダークカラースキーム
//  dynamicColor は使用しない（デザインを安定させるため）
// ─────────────────────────────────────────────
private val NeonExplorerDarkColorScheme = darkColorScheme(
    primary              = NeonCyan,
    onPrimary            = DeepSlate,
    primaryContainer     = NeonCyanGlow,
    onPrimaryContainer   = NeonCyan,

    secondary            = ElectricPurple,
    onSecondary          = DeepSlate,
    secondaryContainer   = Color(0xFF2D1B4E),
    onSecondaryContainer = ElectricPurple,

    tertiary             = GoldBright,
    onTertiary           = DeepSlate,
    tertiaryContainer    = GoldGlow,
    onTertiaryContainer  = GoldBright,

    background           = DeepSlate,
    onBackground         = OnDark,

    surface              = MidnightBlue,
    onSurface            = OnDark,
    surfaceVariant       = SurfaceVariant,
    onSurfaceVariant     = OnDarkSecondary,

    error                = NeonRed,
    onError              = DeepSlate,
    errorContainer       = Color(0xFF3A0A15),
    onErrorContainer     = NeonRed,

    outline              = NeonCyanDim,
    outlineVariant       = SurfaceVariant,

    inverseSurface       = OnDark,
    inverseOnSurface     = DeepSlate,
    inversePrimary       = NeonCyanDim,

    scrim                = Color(0xCC000000)
)

@Composable
fun MysteryWalkAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NeonExplorerDarkColorScheme,
        typography  = Typography,
        content     = content
    )
}
