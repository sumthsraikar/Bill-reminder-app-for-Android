package com.example.myapplicationds.data.local.dao

import androidx.room.*
import com.example.myapplicationds.data.local.entity.PaymentHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentHistoryDao {

    @Query("SELECT * FROM payment_history ORDER BY paidDate DESC")
    fun getAllPaymentHistory(): Flow<List<PaymentHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPayments(payments: List<PaymentHistoryEntity>)

    @Query("DELETE FROM payment_history WHERE billId = :billId")
    suspend fun deleteHistoryForBill(billId: Long)

    @Query("DELETE FROM payment_history")
    suspend fun deleteAllPaymentHistory()
}
