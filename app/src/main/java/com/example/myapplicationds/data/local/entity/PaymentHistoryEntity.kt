package com.example.myapplicationds.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_history")
data class PaymentHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val billId: Long,
    val billName: String = "",
    val category: String = "",
    val paidAmount: Double,
    val paidDate: Long = System.currentTimeMillis()
)
