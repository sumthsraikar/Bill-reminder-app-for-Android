package com.example.myapplicationds.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Electric Blue Accent (#3B82F6)
val PrimaryBlue = Color(0xFF3B82F6)
val PrimaryBlueLight = Color(0xFF60A5FA)
val PrimaryBlueDark = Color(0xFF1D4ED8)

// Status Colors
val StatusPaid = Color(0xFF10B981)       // Emerald Green
val StatusOverdue = Color(0xFFEF4444)    // Electric Red
val StatusUpcoming = Color(0xFFF59E0B)   // Warm Amber/Orange
val StatusDueSoonWarning = Color(0xFFF59E0B)

// Black Glassmorphism Surface & Background Palette
val DarkBackground = Color(0xFF000000)   // Pure Black
val DarkSurface = Color(0xFF0B0B0D)      // Near Black Surface
val DarkSurfaceVariant = Color(0xFF161619)

// Translucent Glass System Tokens
val GlassCardBackground = Color(0x1F1A1A1E) // 10-15% translucent glass
val GlassSurfaceFill = Color(0x12FFFFFF)     // Translucent surface fill
val GlassBorderWhite = Color(0x1AFFFFFF)     // 10% white border
val GlassBorderSubtle = Color(0x0FFFFFFF)    // 5-6% white border
val GlassGlowBlue = Color(0x403B82F6)       // Soft blue glow
val GlassGlowGreen = Color(0x4010B981)      // Soft green glow

// Text Colors
val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFF9CA3AF)    // Soft Gray (15sp medium)
val TextMutedDark = Color(0xFF6B7280)        // Light Gray (14sp)

// Light Theme Fallback Palette
val LightBackground = Color(0xFF000000)      // Enforce Dark Glass by default
val LightSurface = Color(0xFF0B0B0D)
val LightSurfaceVariant = Color(0xFF161619)
val TextPrimaryLight = Color(0xFFFFFFFF)
val TextSecondaryLight = Color(0xFF9CA3AF)

// Curated Category Colors Palette
val CategoryColors = listOf(
    0xFF3B82F6L, // Electric Blue
    0xFF10B981L, // Emerald Green
    0xFFF59E0BL, // Amber / Orange
    0xFFEF4444L, // Red
    0xFF8B5CF6L, // Purple
    0xFFEC4899L, // Pink
    0xFF06B6D4L, // Cyan
    0xFFF43F5EL  // Rose
)

