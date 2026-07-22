package com.example.myapplicationds.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextPrimaryDark,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = TextPrimaryDark,
    secondary = PrimaryBlueLight,
    onSecondary = TextPrimaryDark,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = GlassBorderWhite
)

private val LightColorScheme = DarkColorScheme // Default to Pure Black Glass Theme

val BillBuddyShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),  // 18dp rounded search bar & controls
    large = RoundedCornerShape(24.dp),   // 24dp rounded glass cards as requested!
    extraLarge = RoundedCornerShape(28.dp) // 28dp rounded floating nav bar
)

@Composable
fun BillBuddyTheme(
    themeMode: String = "DARK", // Always prioritize Black Glass UI
    content: @Composable () -> Unit
) {
    val darkTheme = true // Black Glassmorphism theme by default

    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = BillBuddyShapes,
        typography = Typography,
        content = content
    )
}

