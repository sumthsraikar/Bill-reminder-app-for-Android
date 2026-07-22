package com.example.myapplicationds.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val billName: String,
    val category: String,
    val amount: Double,
    val dueDate: Long, // timestamp in millis
    val reminderDate: Long, // timestamp in millis
    val reminderTime: String, // format "HH:mm" e.g., "09:00"
    val recurringType: String, // "None", "Daily", "Weekly", "Monthly", "Yearly"
    val notes: String = "",
    val color: Long = 0xFF2563EBL, // Default Blue Accent
    val icon: String = "Receipt",
    val paymentStatus: String = "UPCOMING", // "UPCOMING", "PAID", "OVERDUE"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
