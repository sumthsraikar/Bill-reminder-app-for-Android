package com.example.myapplicationds.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Returns appropriate ImageVector based on category name or icon key.
 */
fun getCategoryIconByName(category: String, iconKey: String? = null): ImageVector {
    val key = iconKey?.lowercase() ?: category.lowercase()
    return when {
        key.contains("vehicle") || key.contains("car") || key.contains("fuel") || key.contains("transport") || key == "directionscar" -> Icons.Default.DirectionsCar
        key.contains("electricity") || key.contains("power") || key.contains("light") || key == "bolt" || key == "electricbolt" -> Icons.Default.ElectricBolt
        key.contains("mobile") || key.contains("phone") || key.contains("recharge") -> Icons.Default.Smartphone
        key.contains("internet") || key.contains("wifi") || key.contains("broadband") -> Icons.Default.Wifi
        key.contains("investment") || key.contains("bank") || key.contains("stock") || key == "accountbalance" -> Icons.Default.AccountBalance
        key.contains("subscription") || key.contains("membership") -> Icons.Default.Subscriptions
        key.contains("credit") || key.contains("card") || key == "creditcard" -> Icons.Default.CreditCard
        key.contains("rent") || key.contains("house") || key.contains("home") -> Icons.Default.Home
        key.contains("water") || key.contains("utility") -> Icons.Default.WaterDrop
        key.contains("ott") || key.contains("tv") || key.contains("movie") || key.contains("streaming") -> Icons.Default.Tv
        key.contains("insurance") || key.contains("shield") || key.contains("health") -> Icons.Default.Shield
        key.contains("education") || key.contains("school") || key.contains("college") || key.contains("tuition") -> Icons.Default.School
        key.contains("shopping") || key.contains("groceries") -> Icons.Default.ShoppingCart
        else -> Icons.Default.Receipt
    }
}

/**
 * Helper to display category emoji next to category name
 */
fun getCategoryEmoji(category: String): String {
    val catLower = category.lowercase()
    return when {
        catLower.contains("vehicle") || catLower.contains("car") || catLower.contains("fuel") || catLower.contains("transport") -> "🚗"
        catLower.contains("electricity") || catLower.contains("power") || catLower.contains("light") -> "💡"
        catLower.contains("mobile") || catLower.contains("phone") -> "📱"
        catLower.contains("internet") || catLower.contains("wifi") -> "🌐"
        catLower.contains("investment") || catLower.contains("bank") -> "🏦"
        catLower.contains("subscription") -> "🎬"
        catLower.contains("credit") || catLower.contains("card") -> "💳"
        catLower.contains("rent") || catLower.contains("house") || catLower.contains("home") -> "🏠"
        catLower.contains("water") -> "💧"
        catLower.contains("ott") || catLower.contains("tv") -> "📺"
        catLower.contains("insurance") -> "🛡"
        catLower.contains("education") || catLower.contains("school") -> "🎓"
        catLower.contains("shopping") -> "🛒"
        else -> "📄"
    }
}
