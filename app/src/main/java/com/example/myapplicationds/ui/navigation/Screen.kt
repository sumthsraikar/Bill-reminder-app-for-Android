package com.example.myapplicationds.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Bills", Icons.Default.Home)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Statistics : Screen("statistics", "Analytics", Icons.Default.BarChart)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    
    object AddEditBill : Screen("add_edit_bill?billId={billId}", "Bill Details") {
        fun createRoute(billId: Long = -1L): String = "add_edit_bill?billId=$billId"
    }
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Calendar,
    Screen.Statistics,
    Screen.Settings
)
