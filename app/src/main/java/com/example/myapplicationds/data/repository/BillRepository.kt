package com.example.myapplicationds.data.repository

import com.example.myapplicationds.data.local.entity.BillEntity
import com.example.myapplicationds.data.local.entity.CategoryEntity
import com.example.myapplicationds.data.local.entity.PaymentHistoryEntity
import kotlinx.coroutines.flow.Flow

interface BillRepository {
    fun getAllBills(): Flow<List<BillEntity>>
    fun getBillById(id: Long): Flow<BillEntity?>
    suspend fun getBillByIdOneShot(id: Long): BillEntity?
    fun getBillsByStatus(status: String): Flow<List<BillEntity>>
    fun searchBills(query: String): Flow<List<BillEntity>>
    fun getUpcomingCount(): Flow<Int>
    fun getOverdueCount(): Flow<Int>
    fun getPaidCount(): Flow<Int>

    suspend fun insertBill(bill: BillEntity): Long
    suspend fun updateBill(bill: BillEntity)
    suspend fun deleteBill(bill: BillEntity)
    suspend fun markBillAsPaid(bill: BillEntity)

    fun getAllCategories(): Flow<List<CategoryEntity>>
    suspend fun insertCategory(category: CategoryEntity): Long
    suspend fun deleteCategory(category: CategoryEntity)

    fun getAllPaymentHistory(): Flow<List<PaymentHistoryEntity>>
    suspend fun insertPayment(payment: PaymentHistoryEntity): Long

    suspend fun exportBackupJson(): String
    suspend fun importBackupJson(jsonString: String): Boolean
    suspend fun deleteAllData()

    val currencyFlow: Flow<String>
    val themeFlow: Flow<String>
    val notificationEnabledFlow: Flow<Boolean>
    val reminderDaysBeforeFlow: Flow<Int>
    val languageFlow: Flow<String>

    suspend fun setCurrency(currency: String)
    suspend fun setTheme(theme: String)
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun setReminderDaysBefore(days: Int)
    suspend fun setLanguage(language: String)
}
